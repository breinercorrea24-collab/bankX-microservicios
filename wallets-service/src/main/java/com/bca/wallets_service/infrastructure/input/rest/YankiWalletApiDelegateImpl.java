package com.bca.wallets_service.infrastructure.input.rest;

import com.bca.wallets_service.api.WalletsApiDelegate;
import com.bca.wallets_service.domain.model.*;
import com.bca.wallets_service.domain.ports.input.CreateYankiWalletUseCase;
import com.bca.wallets_service.domain.ports.input.LinkWalletToDebitCardUseCase;
import com.bca.wallets_service.domain.ports.input.PayYankiP2PUseCase;
import com.bca.wallets_service.dto.*;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Service
public class YankiWalletApiDelegateImpl implements WalletsApiDelegate {

    private final CreateYankiWalletUseCase createYankiWalletUseCase;
    private final PayYankiP2PUseCase payYankiP2PUseCase;
    private final LinkWalletToDebitCardUseCase linkWalletToDebitCardUseCase;

    public YankiWalletApiDelegateImpl(CreateYankiWalletUseCase createYankiWalletUseCase,
                                      PayYankiP2PUseCase payYankiP2PUseCase,
                                      LinkWalletToDebitCardUseCase linkWalletToDebitCardUseCase) {
        this.createYankiWalletUseCase = createYankiWalletUseCase;
        this.payYankiP2PUseCase = payYankiP2PUseCase;
        this.linkWalletToDebitCardUseCase = linkWalletToDebitCardUseCase;
    }

    @Override
    public Mono<ResponseEntity<YankiWalletResponse>> walletsYankiPost(Mono<YankiWalletRequest> yankiWalletRequest,
        ServerWebExchange exchange) {
        return yankiWalletRequest.flatMap(req ->
            createYankiWalletUseCase.createYankiWallet(req.getCustomerId(), req.getPhone())
                .map(wallet -> {
                    YankiWalletResponse response = new YankiWalletResponse();
                    response.setWalletId(wallet.getWalletId());
                    response.setCustomerId(wallet.getCustomerId());
                    response.setPhone(wallet.getPhone());
                    response.setBalance(wallet.getBalance().floatValue());
                    response.setCurrency(wallet.getCurrency());
                    response.setStatus(YankiWalletResponse.StatusEnum.valueOf(wallet.getStatus().name()));
                    response.setCreatedAt(wallet.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                    Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(Exception.class, e ->
                    Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
        );
    }

    @Override
    public Mono<ResponseEntity<WalletTransactionResponse>> walletsYankiPayPost(Mono<TransferRequest> transferRequest,
        ServerWebExchange exchange) {
        return transferRequest.flatMap(req ->
            payYankiP2PUseCase.pay(req.getFromId(), req.getToId(), BigDecimal.valueOf(req.getAmount()))
                .map(transaction -> {
                    WalletTransactionResponse response = new WalletTransactionResponse();
                    response.setTransactionId(transaction.getTransactionId());
                    response.setFromWalletId(transaction.getFromWalletId());
                    response.setToWalletId(transaction.getToWalletId());
                    response.setAmount(transaction.getAmount().floatValue());
                    response.setCurrency(transaction.getCurrency());
                    response.setBalanceAfter(transaction.getBalanceAfter().floatValue());
                    response.setStatus(WalletTransactionResponse.StatusEnum.valueOf(transaction.getStatus().name()));
                    response.setExecutedAt(transaction.getExecutedAt().atOffset(java.time.ZoneOffset.UTC));
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(InsufficientBalanceException.class, e -> {
                    ErrorResponse error = new ErrorResponse("INSUFFICIENT_BALANCE", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null)); // Note: Adjust if WalletTransactionResponse is not suitable
                })
                .onErrorResume(WalletNotFoundException.class, e ->
                    Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(Exception.class, e ->
                    Mono.just(ResponseEntity.badRequest().build()))
        );
    }

    @Override
    public Mono<ResponseEntity<WalletDebitLinkResponse>> walletsLinkDebitCardPost(Mono<WalletDebitLinkRequest> walletDebitLinkRequest,
        ServerWebExchange exchange) {
        return walletDebitLinkRequest.flatMap(req ->
            linkWalletToDebitCardUseCase.link(req.getWalletId(), req.getWalletType().name(), req.getDebitCardId())
                .map(link -> {
                    WalletDebitLinkResponse response = new WalletDebitLinkResponse();
                    response.setWalletId(link.getWalletId());
                    response.setWalletType(WalletDebitLinkResponse.WalletTypeEnum.valueOf(link.getWalletType().name()));
                    response.setDebitCardId(link.getDebitCardId());
                    response.setMainAccountId(link.getMainAccountId());
                    response.setStatus(WalletDebitLinkResponse.StatusEnum.valueOf(link.getStatus().name()));
                    response.setLinkedAt(link.getLinkedAt().atOffset(java.time.ZoneOffset.UTC));
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(WalletNotFoundException.class, e -> {
                    ErrorResponse error = new ErrorResponse("NOT_FOUND", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                })
                .onErrorResume(DebitCardNotFoundException.class, e -> {
                    ErrorResponse error = new ErrorResponse("NOT_FOUND", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                })
                .onErrorResume(InvalidWalletTypeException.class, e -> {
                    ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                })
                .onErrorResume(Exception.class, e ->
                    Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
        );
    }
}
