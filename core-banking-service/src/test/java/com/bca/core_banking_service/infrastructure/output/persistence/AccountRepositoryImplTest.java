package com.bca.core_banking_service.infrastructure.output.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.AccountMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

    @Mock
    private AccountMongoRepository mongoRepository;

    private AccountRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new AccountRepositoryImpl(mongoRepository);
    }

    @Test
    void save_delegatesToMongoRepository() {
        Account account = sampleAccount("cust-1");
        when(mongoRepository.save(account)).thenReturn(Mono.just(account));

        StepVerifier.create(repository.save(account))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).save(account);
    }

    @Test
    void findById_delegatesToMongoRepository() {
        Account account = sampleAccount("cust-2");
        when(mongoRepository.findById("acc-1")).thenReturn(Mono.just(account));

        StepVerifier.create(repository.findById("acc-1"))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findById("acc-1");
    }

    @Test
    void findByCustomerId_delegatesToMongoRepository() {
        Account account = sampleAccount("cust-3");
        when(mongoRepository.findByCustomerId("cust-3")).thenReturn(Flux.just(account));

        StepVerifier.create(repository.findByCustomerId("cust-3"))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findByCustomerId("cust-3");
    }

    @Test
    void findByCustomerIdAndType_delegatesToMongoRepository() {
        Account account = sampleAccount("cust-4");
        when(mongoRepository.findByCustomerIdAndType("cust-4", AccountType.SAVINGS))
                .thenReturn(Mono.just(account));

        StepVerifier.create(repository.findByCustomerIdAndType("cust-4", AccountType.SAVINGS))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findByCustomerIdAndType("cust-4", AccountType.SAVINGS);
    }

    private SavingsAccount sampleAccount(String customerId) {
        SavingsAccount account = new SavingsAccount(
                customerId,
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100));
        account.setId("acc-1");
        return account;
    }
}
