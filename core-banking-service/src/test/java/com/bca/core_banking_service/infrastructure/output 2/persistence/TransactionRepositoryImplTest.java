package com.bca.core_banking_service.infrastructure.output.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction.TransactionType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.TransactionMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

    @Mock
    private TransactionMongoRepository mongoRepository;

    private TransactionRepositoryImpl repository;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepositoryImpl(mongoRepository);
        transaction = Transaction.builder()
                .id("tx-1")
                .accountId("acc-1")
                .fromAccountId("acc-1")
                .toAccountId("acc-2")
                .type(TransactionType.TRANSFER)
                .amount(BigDecimal.TEN)
                .balance(BigDecimal.valueOf(100))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void save_mapsEntityAndReturnsDomainTransaction() {
        TransactionEntity savedEntity = new TransactionEntity(
                "tx-1",
                "acc-1",
                "acc-1",
                "acc-2",
                TransactionEntity.TransactionType.TRANSFER,
                BigDecimal.TEN,
                BigDecimal.valueOf(100),
                transaction.getTimestamp());

        when(mongoRepository.save(any(TransactionEntity.class))).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(repository.save(transaction))
                .assertNext(saved -> {
                    // All fields should match the saved entity (after mapper round-trip)
                    org.junit.jupiter.api.Assertions.assertEquals(savedEntity.getId(), saved.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(savedEntity.getAccountId(), saved.getAccountId());
                    org.junit.jupiter.api.Assertions.assertEquals(TransactionType.TRANSFER, saved.getType());
                    org.junit.jupiter.api.Assertions.assertEquals(savedEntity.getAmount(), saved.getAmount());
                    org.junit.jupiter.api.Assertions.assertEquals(savedEntity.getBalance(), saved.getBalance());
                })
                .verifyComplete();

        verify(mongoRepository).save(any(TransactionEntity.class));
    }
}
