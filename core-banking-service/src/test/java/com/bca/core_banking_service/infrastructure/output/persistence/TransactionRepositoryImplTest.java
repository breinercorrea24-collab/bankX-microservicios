package com.bca.core_banking_service.infrastructure.output.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.TransactionMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

    @Mock
    private TransactionMongoRepository mongoRepository;

    private TransactionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepositoryImpl(mongoRepository);
    }

    @Test
    void save_mapsDomainToEntityAndBack() {
        LocalDateTime now = LocalDateTime.now();
        Transaction tx = Transaction.builder()
                .id(null)
                .accountId("acc-1")
                .fromAccountId(null)
                .toAccountId(null)
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(BigDecimal.TEN)
                .balance(BigDecimal.valueOf(100))
                .timestamp(now)
                .build();

        when(mongoRepository.save(any(TransactionEntity.class)))
                .thenAnswer(invocation -> {
                    TransactionEntity entity = invocation.getArgument(0);
                    entity.setId("generated-id");
                    return Mono.just(entity);
                });

        StepVerifier.create(repository.save(tx))
                .assertNext(saved -> {
                    assertEquals("generated-id", saved.getId());
                    assertEquals(tx.getAccountId(), saved.getAccountId());
                    assertEquals(tx.getType(), saved.getType());
                    assertEquals(tx.getAmount(), saved.getAmount());
                    assertEquals(tx.getBalance(), saved.getBalance());
                    assertEquals(tx.getTimestamp(), saved.getTimestamp());
                })
                .verifyComplete();

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(mongoRepository).save(captor.capture());
        assertEquals(tx.getAccountId(), captor.getValue().getAccountId());
        assertEquals(tx.getType().name(), captor.getValue().getType().name());
        assertEquals(tx.getAmount(), captor.getValue().getAmount());
    }
}
