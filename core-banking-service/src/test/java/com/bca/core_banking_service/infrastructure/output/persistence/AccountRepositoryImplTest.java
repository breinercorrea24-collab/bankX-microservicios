package com.bca.core_banking_service.infrastructure.output.persistence;

import static org.junit.jupiter.api.Assertions.assertSame;
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
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.AccountMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

    @Mock
    private AccountMongoRepository mongoRepository;

    private AccountRepository repository;

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        repository = new AccountRepositoryImpl(mongoRepository);
        account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100));
        account.setId("acc-1");
    }

    @Test
    void save_returnsResultFromMongoRepository() {
        Mono<SavingsAccount> expected = Mono.just(account);
        when(mongoRepository.save(account)).thenReturn(expected);

        StepVerifier.create(repository.save(account))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).save(account);
    }

    @Test
    void findById_returnsMatchingAccount() {
        when(mongoRepository.findById("acc-1")).thenReturn(Mono.just(account));

        StepVerifier.create(repository.findById("acc-1"))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findById("acc-1");
    }

    @Test
    void findByCustomerId_returnsFluxFromMongoRepository() {
        Flux<Account> expected = Flux.just(account);
        when(mongoRepository.findByCustomerId("customer-1")).thenReturn(expected);

        StepVerifier.create(repository.findByCustomerId("customer-1"))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findByCustomerId("customer-1");
    }

    @Test
    void findByCustomerIdAndType_returnsMonoFromMongoRepository() {
        Mono<Account> expected = Mono.just(account);
        when(mongoRepository.findByCustomerIdAndType("customer-1", AccountType.SAVINGS))
                .thenReturn(expected);

        StepVerifier.create(repository.findByCustomerIdAndType("customer-1", AccountType.SAVINGS))
                .expectNext(account)
                .verifyComplete();

        verify(mongoRepository).findByCustomerIdAndType("customer-1", AccountType.SAVINGS);
    }
}
