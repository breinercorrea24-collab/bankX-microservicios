package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.AccountsApiDelegate;
import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.dto.*;
import com.bca.core_banking_service.infrastructure.input.mapper.AccountApiMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountApiDelegateImpl implements AccountsApiDelegate {

    private final AccountUseCase accountUseCase;

    @Override
    public Mono<ResponseEntity<AccountResponse>> accountsPost(Mono<AccountCreate> accountCreate,
            ServerWebExchange exchange) {
        log.info("Received request to create account");
        return accountCreate
                .doOnNext(request -> log.debug("Account creation request: {}", request))
                .flatMap(request -> accountUseCase.createAccount(
                        request.getCustomerId(),
                        AccountType.valueOf(request.getType().getValue()),
                        request.getCurrency()))
                .map(account -> {
                    log.info("Account created successfully: {}", account);
                    return AccountApiMapper.mapToAccountResponse(account);
                })
                .map(accountResponse -> ResponseEntity.status(HttpStatus.CREATED).body(accountResponse))
                .onErrorResume(RuntimeException.class, ex -> {
                    log.error("Error creating account: {}", ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsAccountIdDepositPost(String accountId,
            Mono<AmountRequest> amountRequest,
            ServerWebExchange exchange) {
        log.info("Received deposit request for accountId: {}", accountId);
        return amountRequest
                .doOnNext(request -> log.debug("Deposit request: {}", request))
                .flatMap(request -> accountUseCase.deposit(accountId, BigDecimal.valueOf(request.getAmount()))
                        .map(account -> {
                            log.info("Deposit successful for accountId: {}, amount: {}", accountId, request.getAmount());
                            return AccountApiMapper.mapToTransactionResponse(account, "DEPOSIT",
                                    BigDecimal.valueOf(request.getAmount()));
                        }))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    log.error("Error processing deposit for accountId: {}: {}", accountId, ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsAccountIdWithdrawPost(String accountId,
            Mono<AmountRequest> amountRequest,
            ServerWebExchange exchange) {
        log.info("Received withdrawal request for accountId: {}", accountId);
        return amountRequest
                .doOnNext(request -> log.debug("Withdrawal request: {}", request))
                .flatMap(request -> accountUseCase.withdraw(accountId, BigDecimal.valueOf(request.getAmount()))
                        .map(account -> {
                            log.info("Withdrawal successful for accountId: {}, amount: {}", accountId, request.getAmount());
                            return AccountApiMapper.mapToTransactionResponse(account, "WITHDRAW",
                                    BigDecimal.valueOf(request.getAmount()));
                        }))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Insufficient funds")) {
                        log.warn("Insufficient funds for withdrawal on accountId: {}", accountId);
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    log.error("Error processing withdrawal for accountId: {}: {}", accountId, ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> accountsTransferPost(Mono<TransferRequest> transferRequest,
            ServerWebExchange exchange) {
        log.info("Received transfer request");
        return transferRequest
                .doOnNext(request -> log.debug("Transfer request: {}", request))
                .flatMap(request -> accountUseCase
                        .transfer(request.getFromId(), request.getToId(), BigDecimal.valueOf(request.getAmount()))
                        .map(account -> {
                            log.info("Transfer successful from accountId: {} to accountId: {}, amount: {}",
                                    request.getFromId(), request.getToId(), request.getAmount());
                            return AccountApiMapper.mapToTransactionResponse(account, "TRANSFER",
                                    BigDecimal.valueOf(request.getAmount()));
                        }))
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Insufficient funds")) {
                        log.warn("Insufficient funds for transfer from accountId: {} to accountId: {}",
                                transferRequest.map(TransferRequest::getFromId),
                                transferRequest.map(TransferRequest::getToId));
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    log.error("Error processing transfer: {}", ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }
}
