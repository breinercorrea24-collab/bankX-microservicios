package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.AccountsApiDelegate;
import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.dto.*;
import com.bca.core_banking_service.infrastructure.input.dto.Account;
import com.bca.core_banking_service.infrastructure.input.mapper.AccountApiMapper;

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
                .map(AccountApiMapper::mapToAccountResponse)
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
                        .map(account -> AccountApiMapper.mapToTransactionResponse(account, "DEPOSIT",
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
                        .map(account -> AccountApiMapper.mapToTransactionResponse(account, "WITHDRAW",
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
                        .map(account -> AccountApiMapper.mapToTransactionResponse(account, "TRANSFER",
                                BigDecimal.valueOf(request.getAmount()))))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Insufficient funds")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

}
