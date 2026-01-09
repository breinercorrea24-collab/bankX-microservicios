package com.bca.core_banking_service.application.usecases;

import com.bca.core_banking_service.domain.model.Account;
import com.bca.core_banking_service.domain.model.Transaction;
import com.bca.core_banking_service.domain.ports.input.AccountUseCase;
import com.bca.core_banking_service.domain.ports.output.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountUseCaseImpl implements AccountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Account> createAccount(String customerId, Account.AccountType type, String currency) {
        return accountRepository.findByCustomerIdAndType(customerId, type)
                .flatMap(existing -> Mono.error(new RuntimeException("Customer already has this account type")))
                .cast(Account.class)
                .switchIfEmpty(Mono.defer(() -> {
                    Account account = new Account();
                    account.setId("acc-" + UUID.randomUUID().toString().substring(0, 8));
                    account.setCustomerId(customerId);
                    account.setType(type);
                    account.setCurrency(currency);
                    account.setBalance(BigDecimal.ZERO);
                    account.setStatus(Account.AccountStatus.ACTIVE);
                    return accountRepository.save(account);
                }));
    }

    @Override
    public Flux<Account> getAccountsByCustomer(String customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Account> deposit(String accountId, BigDecimal amount) {
        return accountRepository.findById(accountId)
                .flatMap(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    return accountRepository.save(account)
                            .flatMap(savedAccount -> {
                                Transaction transaction = new Transaction();
                                transaction.setId("tx-dep-" + UUID.randomUUID().toString().substring(0, 8));
                                transaction.setAccountId(accountId);
                                transaction.setType(Transaction.TransactionType.DEPOSIT);
                                transaction.setAmount(amount);
                                transaction.setBalance(savedAccount.getBalance());
                                transaction.setTimestamp(LocalDateTime.now());
                                return transactionRepository.save(transaction)
                                        .thenReturn(savedAccount);
                            });
                });
    }

    @Override
    public Mono<Account> withdraw(String accountId, BigDecimal amount) {
        return accountRepository.findById(accountId)
                .flatMap(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new RuntimeException("Insufficient funds"));
                    }
                    account.setBalance(account.getBalance().subtract(amount));
                    return accountRepository.save(account)
                            .flatMap(savedAccount -> {
                                Transaction transaction = new Transaction();
                                transaction.setId("tx-wd-" + UUID.randomUUID().toString().substring(0, 8));
                                transaction.setAccountId(accountId);
                                transaction.setType(Transaction.TransactionType.WITHDRAW);
                                transaction.setAmount(amount);
                                transaction.setBalance(savedAccount.getBalance());
                                transaction.setTimestamp(LocalDateTime.now());
                                return transactionRepository.save(transaction)
                                        .thenReturn(savedAccount);
                            });
                });
    }

    @Override
    public Mono<Account> transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        return accountRepository.findById(fromAccountId)
                .zipWith(accountRepository.findById(toAccountId))
                .flatMap(tuple -> {
                    Account fromAccount = tuple.getT1();
                    Account toAccount = tuple.getT2();

                    if (fromAccount.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new RuntimeException("Insufficient funds"));
                    }

                    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                    toAccount.setBalance(toAccount.getBalance().add(amount));

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
                                return transactionRepository.save(transaction)
                                        .thenReturn(fromAccount);
                            }));
                });
    }
}