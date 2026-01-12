package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.api.BootcoinApiDelegate;
import com.bca.bootcoin_service.domain.ports.input.AcceptBootCoinTradeUseCase;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinTradeUseCase;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptResponse;
import com.bca.bootcoin_service.dto.BootCoinTradeRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeResponse;
import com.bca.bootcoin_service.infrastructure.input.rest.mapper.BootCoinTradeApiMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BootCoinTradeApiDelegateImpl implements BootcoinApiDelegate {

    private final CreateBootCoinTradeUseCase createBootCoinTradeUseCase;
    private final AcceptBootCoinTradeUseCase acceptBootCoinTradeUseCase;
    private final BootCoinTradeApiMapper mapper;

    public BootCoinTradeApiDelegateImpl(CreateBootCoinTradeUseCase createBootCoinTradeUseCase,
            AcceptBootCoinTradeUseCase acceptBootCoinTradeUseCase, BootCoinTradeApiMapper mapper) {
        this.createBootCoinTradeUseCase = createBootCoinTradeUseCase;
        this.acceptBootCoinTradeUseCase = acceptBootCoinTradeUseCase;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<BootCoinTradeResponse>> bootcoinTradesPost(
            Mono<BootCoinTradeRequest> bootCoinTradeRequest,
            ServerWebExchange exchange) {

        return bootCoinTradeRequest
                // Si no viene body → 400
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))

                // flatMap SIEMPRE debe devolver Mono
                .flatMap(request -> {
                    if (request.getSellerWalletId() == null || request.getSellerWalletId().isBlank()) {
                        return Mono.error(new IllegalArgumentException("sellerWalletId is required"));
                    }

                    if (request.getTradeType() == null) {
                        return Mono.error(new IllegalArgumentException("tradeType is required"));
                    }

                    return createBootCoinTradeUseCase.createTrade(
                            request.getSellerWalletId(),
                            mapper.toDomainTradeType(request.getTradeType()),
                            mapper.toBigDecimal(request.getAmountBTC()),
                            mapper.toBigDecimal(request.getPricePEN()));
                })

                // Mapear dominio → response
                .map(trade -> ResponseEntity.status(201).body(mapper.toResponse(trade)))

                // Errores de validación → 400
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Override
    public Mono<ResponseEntity<BootCoinTradeAcceptResponse>> bootcoinTradesTradeIdAcceptPost(
            String tradeId,
            Mono<BootCoinTradeAcceptRequest> bootCoinTradeAcceptRequest,
            ServerWebExchange exchange) {

        return bootCoinTradeAcceptRequest
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body required")))
                .flatMap(req -> {
                    String buyerWalletId = req.getBuyerWalletId();

                    if (buyerWalletId == null || buyerWalletId.isBlank()) {
                        return Mono.error(new IllegalArgumentException("buyerWalletId is required"));
                    }

                    return acceptBootCoinTradeUseCase.acceptTrade(tradeId, buyerWalletId);
                })
                .map(trade -> ResponseEntity.ok(mapper.toAcceptResponse(trade)))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
