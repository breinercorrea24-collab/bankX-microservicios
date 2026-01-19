package com.bca.core_banking_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.ports.output.event.AccountEventPublisher;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.TransactionRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AccountUseCaseImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountEventPublisher accountEventPublisher;

    @Mock
    private ExternalCardsWebClientAdapter externalCardsClient;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository creditRepository;

    private AccountUseCaseImpl accountUseCase;

    @BeforeEach
    void setUp() {
        accountUseCase = new AccountUseCaseImpl(
                transactionRepository,
                accountEventPublisher,
                externalCardsClient,
                accountRepository,
                creditRepository);

        lenient().when(creditRepository.hasOverdueCredits(any()))
                .thenReturn(Mono.just(false));
        lenient().when(accountRepository.findByCustomerId(any()))
                .thenReturn(Flux.empty());
    }

    @Test
    void createAccount_whenCustomerHasNoAccount_createsNewAccount() {
        String customerId = "customer-123";

        when(accountRepository.findByCustomerIdAndType(customerId, AccountType.SAVINGS))
                .thenReturn(Mono.empty());
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account saved = invocation.getArgument(0);
                    saved.setId("generated-id");
                    return Mono.just(saved);
                });

        StepVerifier.create(accountUseCase.createAccount(customerId, CustomerType.PERSONAL, AccountType.SAVINGS, "USD"))
                .assertNext(account -> {
                    assertEquals("generated-id", account.getId());
                    assertEquals(customerId, account.getCustomerId());
                    assertEquals(AccountType.SAVINGS, account.getType());
                })
                .verifyComplete();

        verify(accountRepository).findByCustomerIdAndType(customerId, AccountType.SAVINGS);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_whenAccountAlreadyExists_emitsBusinessException() {
        String customerId = "customer-456";
        Account existing = buildSavingsAccount("acc-1", customerId, BigDecimal.TEN, ProductStatus.ACTIVE);

        when(accountRepository.findByCustomerIdAndType(customerId, AccountType.SAVINGS))
                .thenReturn(Mono.just(existing));

        StepVerifier.create(accountUseCase.createAccount(customerId, CustomerType.PERSONAL, AccountType.SAVINGS, "USD"))
                .expectError(BusinessException.class)
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountsByCustomer_whenAccountsExist_returnsFlux() {
        String customerId = "customer-789";
        Account account = buildSavingsAccount("acc-2", customerId, BigDecimal.valueOf(50), ProductStatus.ACTIVE);

        when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.just(account));

        StepVerifier.create(accountUseCase.getAccountsByCustomer(customerId))
                .expectNext(account)
                .verifyComplete();
    }

    @Test
    void getAccountsByCustomer_whenNoAccountsFound_emitsBusinessException() {
        String customerId = "customer-000";
        when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.empty());

        StepVerifier.create(accountUseCase.getAccountsByCustomer(customerId))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void deposit_whenAccountExists_updatesBalanceAndPublishesEvent() {
        String accountId = "acc-10";
        SavingsAccount account = buildSavingsAccount(accountId, "customer-1", BigDecimal.valueOf(100), ProductStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Mono.just(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        BigDecimal amount = BigDecimal.valueOf(50);

        StepVerifier.create(accountUseCase.deposit(accountId, amount))
                .assertNext(updated -> assertEquals(BigDecimal.valueOf(150), updated.getBalance()))
                .verifyComplete();

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertEquals(Transaction.TransactionType.DEPOSIT, txCaptor.getValue().getType());
        assertEquals(amount, txCaptor.getValue().getAmount());
        assertEquals(BigDecimal.valueOf(150), txCaptor.getValue().getBalance());

        ArgumentCaptor<AccountDepositEvent> eventCaptor = ArgumentCaptor.forClass(AccountDepositEvent.class);
        verify(accountEventPublisher).publishDeposit(eventCaptor.capture());
        assertEquals(accountId, eventCaptor.getValue().accountId);
        assertEquals(amount, eventCaptor.getValue().amount);
        assertEquals(BigDecimal.valueOf(150), eventCaptor.getValue().newBalance);
    }

    @Test
    void deposit_whenAccountInactive_emitsBusinessException() {
        String accountId = "acc-inactive";
        SavingsAccount account = buildSavingsAccount(accountId, "customer-1", BigDecimal.valueOf(50), ProductStatus.BLOCKED);

        when(accountRepository.findById(accountId)).thenReturn(Mono.just(account));

        StepVerifier.create(accountUseCase.deposit(accountId, BigDecimal.TEN))
                .expectError(BusinessException.class)
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountEventPublisher, never()).publishDeposit(any(AccountDepositEvent.class));
    }

    @Test
    void deposit_whenAccountNotFound_emitsAccountNotFoundException() {
        when(accountRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(accountUseCase.deposit("missing", BigDecimal.TEN))
                .expectError(AccountNotFoundException.class)
                .verify();
    }

    @Test
    void withdraw_whenAccountExists_updatesBalanceAndPublishesEvent() {
        String accountId = "acc-20";
        SavingsAccount account = buildSavingsAccount(accountId, "customer-1", BigDecimal.valueOf(200), ProductStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Mono.just(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        BigDecimal amount = BigDecimal.valueOf(125);

        StepVerifier.create(accountUseCase.withdraw(accountId, amount))
                .assertNext(updated -> assertEquals(BigDecimal.valueOf(75), updated.getBalance()))
                .verifyComplete();

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertEquals(Transaction.TransactionType.WITHDRAW, txCaptor.getValue().getType());
        assertEquals(amount, txCaptor.getValue().getAmount());
        assertEquals(BigDecimal.valueOf(75), txCaptor.getValue().getBalance());

        ArgumentCaptor<AccountWithdrawalEvent> eventCaptor = ArgumentCaptor.forClass(AccountWithdrawalEvent.class);
        verify(accountEventPublisher).publishWithdraw(eventCaptor.capture());
        assertEquals(accountId, eventCaptor.getValue().accountId);
        assertEquals(amount, eventCaptor.getValue().amount);
        assertEquals(BigDecimal.valueOf(75), eventCaptor.getValue().newBalance);
    }

    @Test
    void withdraw_whenInsufficientFunds_emitsRuntimeException() {
        String accountId = "acc-30";
        SavingsAccount account = buildSavingsAccount(accountId, "customer-1", BigDecimal.valueOf(20), ProductStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Mono.just(account));

        StepVerifier.create(accountUseCase.withdraw(accountId, BigDecimal.valueOf(50)))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof RuntimeException);
                    assertTrue(error.getMessage().contains("Saldo insuficiente"));
                })
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountEventPublisher, never()).publishWithdraw(any(AccountWithdrawalEvent.class));
    }

    @Test
    void withdraw_whenAccountInactive_emitsBusinessException() {
        String accountId = "acc-inactive";
        SavingsAccount account = buildSavingsAccount(accountId, "customer-1", BigDecimal.valueOf(100), ProductStatus.BLOCKED);

        when(accountRepository.findById(accountId)).thenReturn(Mono.just(account));

        StepVerifier.create(accountUseCase.withdraw(accountId, BigDecimal.TEN))
                .expectError(BusinessException.class)
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountEventPublisher, never()).publishWithdraw(any(AccountWithdrawalEvent.class));
    }

    @Test
    void transfer_whenEnoughBalance_movesFundsAndRecordsTransaction() {
        String fromAccountId = "acc-from";
        String toAccountId = "acc-to";
        SavingsAccount fromAccount = buildSavingsAccount(fromAccountId, "customer-1", BigDecimal.valueOf(300), ProductStatus.ACTIVE);
        SavingsAccount toAccount = buildSavingsAccount(toAccountId, "customer-2", BigDecimal.valueOf(25), ProductStatus.ACTIVE);

        when(accountRepository.findById(fromAccountId)).thenReturn(Mono.just(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Mono.just(toAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        BigDecimal amount = BigDecimal.valueOf(120);

        StepVerifier.create(accountUseCase.transfer(fromAccountId, toAccountId, amount))
                .assertNext(result -> assertEquals(BigDecimal.valueOf(180), result.getBalance()))
                .verifyComplete();

        assertEquals(BigDecimal.valueOf(145), toAccount.getBalance());

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertEquals(Transaction.TransactionType.TRANSFER, txCaptor.getValue().getType());
        assertEquals(fromAccountId, txCaptor.getValue().getFromAccountId());
        assertEquals(toAccountId, txCaptor.getValue().getToAccountId());
        assertEquals(amount, txCaptor.getValue().getAmount());

        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transfer_whenInsufficientFunds_emitsBusinessException() {
        String fromAccountId = "acc-from-2";
        String toAccountId = "acc-to-2";
        SavingsAccount fromAccount = buildSavingsAccount(fromAccountId, "customer-1", BigDecimal.valueOf(10), ProductStatus.ACTIVE);
        SavingsAccount toAccount = buildSavingsAccount(toAccountId, "customer-2", BigDecimal.ZERO, ProductStatus.ACTIVE);

        when(accountRepository.findById(fromAccountId)).thenReturn(Mono.just(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Mono.just(toAccount));

        StepVerifier.create(accountUseCase.transfer(fromAccountId, toAccountId, BigDecimal.valueOf(20)))
                .expectError(BusinessException.class)
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private SavingsAccount buildSavingsAccount(String id, String customerId, BigDecimal balance, ProductStatus status) {
        SavingsAccount account = new SavingsAccount(
                customerId,
                "USD",
                status,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                balance);
        account.setId(id);
        return account;
    }
}
