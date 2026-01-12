package com.bca.core_banking_service.application.usecases;

import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.ports.output.event.AccountEventPublisher;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.TransactionRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Account;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;

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

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountEventPublisher kafkaProducer;

    @Override
    public Mono<Account> createAccount(String customerId, Account.AccountType type, String currency) {
        log.info("Creating account for customerId: {}, type: {}, currency: {}", customerId, type, currency);
        return accountRepository.findByCustomerIdAndType(customerId, type)
                .flatMap(existing -> {
                    log.warn("Customer already has this account type: {}", type);
                    return Mono.error(new RuntimeException("Customer already has this account type"));
                })
                .cast(Account.class)
                .switchIfEmpty(Mono.defer(() -> {
                    Account account = new Account();
                    account.setId("acc-" + UUID.randomUUID().toString().substring(0, 8));
                    account.setCustomerId(customerId);
                    account.setType(type);
                    account.setCurrency(currency);
                    account.setBalance(BigDecimal.ZERO);
                    account.setStatus(Account.AccountStatus.ACTIVE);
                    log.info("Saving new account: {}", account);
                    return accountRepository.save(account);
                }));
    }

    @Override
    public Flux<Account> getAccountsByCustomer(String customerId) {
        log.info("Fetching accounts for customerId: {}", customerId);
        return accountRepository.findByCustomerId(customerId);
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
                .flatMap(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        log.warn("Insufficient funds for accountId: {}", accountId);
                        return Mono.error(new RuntimeException("Insufficient funds"));
                    }
                    account.setBalance(account.getBalance().subtract(amount));
                    log.info("Account {} new balance after withdrawal: {}", accountId, account.getBalance());
                    return accountRepository.save(account)
                            .flatMap(savedAccount -> {
                                Transaction transaction = new Transaction();
                                transaction.setId("tx-wd-" + UUID.randomUUID().toString().substring(0, 8));
                                transaction.setAccountId(accountId);
                                transaction.setType(Transaction.TransactionType.WITHDRAW);
                                transaction.setAmount(amount);
                                transaction.setBalance(savedAccount.getBalance());
                                transaction.setTimestamp(LocalDateTime.now());
                                log.info("Recording withdrawal transaction: {}", transaction);
                                return transactionRepository.save(transaction)
                                        .thenReturn(savedAccount);
                            });
                });
    }

    @Override
    public Mono<Account> transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        log.info("Transferring amount: {} from accountId: {} to accountId: {}", amount, fromAccountId, toAccountId);
        return accountRepository.findById(fromAccountId)
                .zipWith(accountRepository.findById(toAccountId))
                .flatMap(tuple -> {
                    Account fromAccount = tuple.getT1();
                    Account toAccount = tuple.getT2();

                    if (fromAccount.getBalance().compareTo(amount) < 0) {
                        log.warn("Insufficient funds for transfer from accountId: {}", fromAccountId);
                        return Mono.error(new RuntimeException("Insufficient funds"));
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