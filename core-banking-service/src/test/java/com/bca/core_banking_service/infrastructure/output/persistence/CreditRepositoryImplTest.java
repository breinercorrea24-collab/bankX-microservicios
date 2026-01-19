package com.bca.core_banking_service.infrastructure.output.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.CreditEntity;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.CreditMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreditRepositoryImplTest {

    @Mock
    private CreditMongoRepository mongoRepository;

    private CreditRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CreditRepositoryImpl(mongoRepository);
    }

    @Test
    void findByCustomerId_returnsMappedDomainCredit() {
        CreditEntity entity = sampleEntity();
        when(mongoRepository.findByCustomerId("cust-1")).thenReturn(Mono.just(entity));

        StepVerifier.create(repository.findByCustomerId("cust-1"))
                .assertNext(credit -> {
                    assertEquals(entity.getId(), credit.getId());
                    assertEquals(entity.getCustomerId(), credit.getCustomerId());
                    assertEquals(Credit.CreditType.valueOf(entity.getCreditType().name()), credit.getCreditType());
                    assertEquals(entity.getPendingDebt(), credit.getPendingDebt());
                })
                .verifyComplete();

        verify(mongoRepository).findByCustomerId("cust-1");
    }

    @Test
    void findById_mapsEntityToDomain() {
        CreditEntity entity = sampleEntity();
        when(mongoRepository.findById("cred-1")).thenReturn(Mono.just(entity));

        StepVerifier.create(repository.findById("cred-1"))
                .assertNext(credit -> {
                    assertEquals(entity.getId(), credit.getId());
                    assertEquals(entity.getCustomerId(), credit.getCustomerId());
                    assertEquals(Credit.CreditType.valueOf(entity.getCreditType().name()), credit.getCreditType());
                    assertEquals(entity.getOriginalAmount(), credit.getOriginalAmount());
                    assertEquals(entity.getDueDate(), credit.getDueDate());
                })
                .verifyComplete();

        verify(mongoRepository).findById("cred-1");
    }

    @Test
    void save_mapsDomainToEntityAndBack() {
        LocalDateTime now = LocalDateTime.now();
        Credit credit = new Credit(
                null,
                "cust-2",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(1800),
                BigDecimal.valueOf(7.5),
                18,
                Credit.CreditStatus.ACTIVE,
                now.minusDays(2),
                now.plusMonths(18));

        when(mongoRepository.save(any(CreditEntity.class)))
                .thenAnswer(invocation -> {
                    CreditEntity saved = invocation.getArgument(0);
                    saved.setId("generated-id");
                    return Mono.just(saved);
                });

        StepVerifier.create(repository.save(credit))
                .assertNext(saved -> {
                    assertEquals("generated-id", saved.getId());
                    assertEquals(credit.getCustomerId(), saved.getCustomerId());
                    assertEquals(credit.getOriginalAmount(), saved.getOriginalAmount());
                    assertEquals(credit.getPendingDebt(), saved.getPendingDebt());
                    assertEquals(credit.getDueDate(), saved.getDueDate());
                })
                .verifyComplete();

        ArgumentCaptor<CreditEntity> captor = ArgumentCaptor.forClass(CreditEntity.class);
        verify(mongoRepository).save(captor.capture());
        assertEquals(credit.getCustomerId(), captor.getValue().getCustomerId());
        assertEquals(credit.getOriginalAmount(), captor.getValue().getOriginalAmount());
    }

    @Test
    void countByCustomerId_delegatesToMongoRepository() {
        when(mongoRepository.countByCustomerId("cust-2")).thenReturn(Mono.just(3L));

        StepVerifier.create(repository.countByCustomerId("cust-2"))
                .expectNext(3L)
                .verifyComplete();

        verify(mongoRepository).countByCustomerId("cust-2");
    }

    @Test
    void hasOverdueCredits_usesActiveStatusAndCurrentDate() {
        when(mongoRepository.existsByCustomerIdAndStatusAndDueDateBefore(
                eq("cust-3"), eq(CreditStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(repository.hasOverdueCredits("cust-3"))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<CreditStatus> statusCaptor = ArgumentCaptor.forClass(CreditStatus.class);
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(mongoRepository).existsByCustomerIdAndStatusAndDueDateBefore(
                eq("cust-3"),
                statusCaptor.capture(),
                dateCaptor.capture());

        assertEquals(CreditStatus.ACTIVE, statusCaptor.getValue());
        assertNotNull(dateCaptor.getValue());
    }

    private CreditEntity sampleEntity() {
        LocalDateTime now = LocalDateTime.now();
        return new CreditEntity(
                "cred-1",
                "cust-1",
                CreditEntity.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(800),
                BigDecimal.valueOf(5),
                12,
                CreditEntity.CreditStatus.ACTIVE,
                now.minusDays(1),
                now.plusMonths(12));
    }
}
