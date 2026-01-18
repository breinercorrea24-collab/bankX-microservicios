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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.CreditEntity;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.CreditMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreditRepositoryImplTest {

    @Mock
    private CreditMongoRepository mongoRepository;

    private CreditRepositoryImpl repository;

    private Credit credit;
    private CreditEntity entity;

    @BeforeEach
    void setUp() {
        repository = new CreditRepositoryImpl(mongoRepository);
        credit = new Credit(
                "cred-1",
                "cus-1",
                CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(12),
                12,
                CreditStatus.ACTIVE,
                LocalDateTime.now());

        entity = new CreditEntity(
                "cred-1",
                "cus-1",
                CreditEntity.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(12),
                12,
                CreditEntity.CreditStatus.ACTIVE,
                credit.getCreatedAt());
    }

    @Test
    void save_persistsAndReturnsDomainCredit() {
        when(mongoRepository.save(any(CreditEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(repository.save(credit))
                .assertNext(saved -> {
                    assertEquals(credit.getId(), saved.getId());
                    assertEquals(credit.getCustomerId(), saved.getCustomerId());
                    assertEquals(credit.getCreditType(), saved.getCreditType());
                })
                .verifyComplete();

        verify(mongoRepository).save(any(CreditEntity.class));
    }

    @Test
    void findById_returnsDomainCreditWhenFound() {
        when(mongoRepository.findById("cred-1")).thenReturn(Mono.just(entity));

        StepVerifier.create(repository.findById("cred-1"))
                .assertNext(found -> assertEquals(credit.getCustomerId(), found.getCustomerId()))
                .verifyComplete();

        verify(mongoRepository).findById("cred-1");
    }
}
