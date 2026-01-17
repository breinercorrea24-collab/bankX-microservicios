package com.bca.core_banking_service.application.usecases;

import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.application.usecases.factory.AccountFactory;
import com.bca.core_banking_service.application.usecases.factory.CreateAccountCommand;
import com.bca.core_banking_service.application.usecases.validation.ValidationProduct;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.ports.output.event.AccountEventPublisher;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.TransactionRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUseCaseImpl implements AccountUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountEventPublisher kafkaProducer;

    private final ExternalCardsWebClientAdapter externalCardsClient;
    private final AccountRepository accountRepository;
    private final CreditRepository creditRepository;

    @Override
    public Mono<Account> createAccount(String customerId, AccountType type, String currency) {
        log.info("Starting account creation for customerId: {}, type: {}, currency: {}", customerId, type, currency);
        return accountRepository
                .findByCustomerIdAndType(customerId, type)
                .flatMap(acc -> {
                    log.info("Customer already has account type: {}", type);
                    return Mono.error(new BusinessException(
                            "Customer already has account type " + type)).cast(Account.class);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No existing account found for customerId: {}, type: {}. Proceeding with creation.",
                        customerId, type);

                    ValidationProduct validationProduct = new ValidationProduct(externalCardsClient, accountRepository, creditRepository);

                    // Chain the validation Mono so it is subscribed and its result enforced
                    return validationProduct.validateAccountCreation(customerId, type, null)
                        .then(Mono.fromSupplier(() -> {
                        Account account = AccountFactory.create(new CreateAccountCommand(
                            customerId,
                            CustomerType.PERSONAL, // Falta al momento de crear la cuenta el tipo de cliente
                            type,
                            currency));
                        log.info("Saving account for customerId: {}, type: {}", customerId, type);
                        return account;
                        }))
                        .flatMap(accountRepository::save);
                }));
    }

    @Override
    public Flux<Account> getAccountsByCustomer(String customerId) {
        log.info("Fetching accounts for customerId: {}", customerId);
        return accountRepository.findByCustomerId(customerId)
                .switchIfEmpty(Flux.error(new BusinessException("No accounts found for customerId: " + customerId)));
    }

    @Override
    public Mono<Account> deposit(String accountId, BigDecimal amount) {
        log.info("Depositing amount: {} to accountId: {}", amount, accountId);
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account not found")))
                .doOnNext(acc -> {
                    log.info("Account retrieved: {}", acc);
                    if (!acc.isActive()) {
                        log.info("Account {} is not active", accountId);
                        throw new BusinessException("Account not active");
                    }
                })
                .map(acc -> {
                    acc.deposit(amount);
                    log.info("Account {} new balance after deposit: {}", accountId, acc.getBalance());
                    return acc;
                })
                .flatMap(accountRepository::save)
                .flatMap(acc -> {
                    Transaction tx = Transaction.builder()
                            .id(UUID.randomUUID().toString())
                            .accountId(acc.getId())
                            .type(Transaction.TransactionType.DEPOSIT)
                            .amount(amount)
                            .balance(acc.getBalance())
                            .timestamp(LocalDateTime.now())
                            .build();
                    log.info("Recording transaction: {}", tx);
                    return transactionRepository.save(tx)
                            .thenReturn(acc);
                })
                .doOnNext(acc -> {
                    log.info("Publishing deposit event for accountId: {}", accountId);
                    kafkaProducer.publishDeposit(
                            new AccountDepositEvent(
                                    acc.getId(),
                                    amount,
                                    acc.getBalance()));
                });
    }

    @Override
    public Mono<Account> withdraw(String accountId, BigDecimal amount) {
        log.info("Withdrawing amount: {} from accountId: {}", amount, accountId);
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account not found")))
                .doOnNext(acc -> {
                    log.info("Account retrieved: {}", acc);
                    if (!acc.isActive()) {
                        log.info("Account {} is not active", accountId);
                        throw new BusinessException("Account not active");
                    }
                })
                .map(acc -> {
                    acc.withdraw(amount);
                    log.info("Account {} new balance after withdraw: {}", accountId, acc.getBalance());
                    return acc;
                })
                .flatMap(accountRepository::save)
                .flatMap(acc -> {
                    Transaction tx = Transaction.builder()
                            .id(UUID.randomUUID().toString())
                            .accountId(acc.getId())
                            .type(Transaction.TransactionType.WITHDRAW)
                            .amount(amount)
                            .balance(acc.getBalance())
                            .timestamp(LocalDateTime.now())
                            .build();
                    log.info("Recording transaction: {}", tx);
                    return transactionRepository.save(tx)
                            .thenReturn(acc);
                })
                .doOnNext(acc -> {
                    log.info("Publishing withdrawal event for accountId: {}", accountId);
                    kafkaProducer.publishWithdraw(
                            new AccountWithdrawalEvent(
                                    acc.getId(),
                                    amount,
                                    acc.getBalance()));
                });
    }

    @Override
    public Mono<Account> transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        log.info("Transferring amount: {} from accountId: {} to accountId: {}", amount, fromAccountId, toAccountId);
        return accountRepository.findById(fromAccountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("From account not found: " + fromAccountId)))
                .zipWith(accountRepository.findById(toAccountId)
                        .switchIfEmpty(
                                Mono.error(new AccountNotFoundException("To account not found: " + toAccountId))))
                .flatMap(tuple -> {
                    Account fromAccount = tuple.getT1();
                    Account toAccount = tuple.getT2();

                    if (fromAccount.getBalance().compareTo(amount) < 0) {
                        log.warn("Insufficient funds for transfer from accountId: {}", fromAccountId);
                        return Mono.error(new BusinessException("Insufficient funds for transfer"));
                    }

                    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                    toAccount.setBalance(toAccount.getBalance().add(amount));

                    log.info("Updated balances - fromAccountId: {}, balance: {}, toAccountId: {}, balance: {}",
                            fromAccountId, fromAccount.getBalance(), toAccountId, toAccount.getBalance());

                    return accountRepository.save(fromAccount)
                            .and(accountRepository.save(toAccount))
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setId("tx-tr-" + UUID.randomUUID().toString().substring(0, 8));
                                transaction.setFromAccountId(fromAccountId);
                                transaction.setToAccountId(toAccountId);
                                transaction.setType(Transaction.TransactionType.TRANSFER);
                                transaction.setAmount(amount);
                                transaction.setBalance(fromAccount.getBalance());
                                transaction.setTimestamp(LocalDateTime.now());
                                log.info("Recording transfer transaction: {}", transaction);
                                return transactionRepository.save(transaction)
                                        .thenReturn(fromAccount);
                            }));
                });
    }
}