package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.AccountsApiDelegate;
import com.bca.core_banking_service.domain.ports.input.AccountUseCase;
import com.bca.core_banking_service.dto.*;
import com.bca.core_banking_service.infrastructure.input.dto.Account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountApiDelegateImpl implements AccountsApiDelegate {

    private final AccountUseCase accountUseCase;

    @Override
    public Mono<ResponseEntity<AccountResponse>> accountsPost(Mono<AccountCreate> accountCreate,
            ServerWebExchange exchange) {
        return accountCreate
                .flatMap(request -> accountUseCase.createAccount(
                        request.getCustomerId(),
                        Account.AccountType.valueOf(request.getType().getValue()),
                        request.getCurrency()))
                .map(this::mapToAccountResponse)
                .map(accountResponse -> ResponseEntity.status(HttpStatus.CREATED).body(accountResponse))
                .onErrorResume(RuntimeException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsAccountIdDepositPost(String accountId,
            Mono<AmountRequest> amountRequest,
            ServerWebExchange exchange) {
        return amountRequest
                .flatMap(request -> accountUseCase.deposit(accountId, BigDecimal.valueOf(request.getAmount()))
                        .map(account -> mapToTransactionResponse(account, "DEPOSIT",
                                BigDecimal.valueOf(request.getAmount()))))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsAccountIdWithdrawPost(String accountId,
            Mono<AmountRequest> amountRequest,
            ServerWebExchange exchange) {
        return amountRequest
                .flatMap(request -> accountUseCase.withdraw(accountId, BigDecimal.valueOf(request.getAmount()))
                        .map(account -> mapToTransactionResponse(account, "WITHDRAW",
                                BigDecimal.valueOf(request.getAmount()))))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Insufficient funds")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsTransferPost(Mono<TransferRequest> transferRequest,
            ServerWebExchange exchange) {
        return transferRequest
                .flatMap(request -> accountUseCase
                        .transfer(request.getFromId(), request.getToId(), BigDecimal.valueOf(request.getAmount()))
                        .map(account -> mapToTransactionResponse(account, "TRANSFER",
                                BigDecimal.valueOf(request.getAmount()))))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Insufficient funds")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setType(AccountResponse.TypeEnum.valueOf(account.getType().name()));
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance().floatValue());
        response.setStatus(AccountResponse.StatusEnum.valueOf(account.getStatus().name()));
        return response;
    }

    private TransactionResponse mapToTransactionResponse(Account account, String type, BigDecimal amount) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId("tx-" + System.currentTimeMillis());
        response.setAccountId(account.getId());
        response.setType(TransactionResponse.TypeEnum.valueOf(type));
        response.setAmount(amount.floatValue());
        response.setBalance(account.getBalance().floatValue());
        response.setTimestamp(java.time.OffsetDateTime.now());
        return response;
    }
}
