package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.api.BootcoinApiDelegate;
import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.ports.input.AcceptBootCoinTradeUseCase;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinTradeUseCase;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptResponse;
import com.bca.bootcoin_service.dto.BootCoinTradeRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeResponse;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BootCoinTradeApiDelegateImpl implements BootcoinApiDelegate {

    private final CreateBootCoinTradeUseCase createBootCoinTradeUseCase;
    private final AcceptBootCoinTradeUseCase acceptBootCoinTradeUseCase;

    public BootCoinTradeApiDelegateImpl(CreateBootCoinTradeUseCase createBootCoinTradeUseCase,
            AcceptBootCoinTradeUseCase acceptBootCoinTradeUseCase) {
        this.createBootCoinTradeUseCase = createBootCoinTradeUseCase;
        this.acceptBootCoinTradeUseCase = acceptBootCoinTradeUseCase;
    }

    @Override
    public Mono<ResponseEntity<BootCoinTradeResponse>> bootcoinTradesPost(
            Mono<BootCoinTradeRequest> bootCoinTradeRequest,
            ServerWebExchange exchange) {

        return bootCoinTradeRequest
                // Si no viene body → 400
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))

                // flatMap SIEMPRE debe devolver Mono
                .flatMap((BootCoinTradeRequest request) -> {

                    if (request.getSellerWalletId() == null || request.getSellerWalletId().isBlank()) {
                        return Mono.error(new IllegalArgumentException("sellerWalletId is required"));
                    }

                    if (request.getTradeType() == null) {
                        return Mono.error(new IllegalArgumentException("tradeType is required"));
                    }

                    BootCoinTrade.TradeType tradeType = BootCoinTrade.TradeType.valueOf(request.getTradeType().name());

                    BigDecimal amount = request.getAmountBTC() != null
                            ? BigDecimal.valueOf(request.getAmountBTC())
                            : null;

                    BigDecimal price = request.getPricePEN() != null
                            ? BigDecimal.valueOf(request.getPricePEN())
                            : null;

                    return createBootCoinTradeUseCase.createTrade(
                            request.getSellerWalletId(),
                            tradeType,
                            amount,
                            price);
                })

                // Mapear dominio → response
                .map(trade -> {
                    BootCoinTradeResponse response = new BootCoinTradeResponse();
                    response.setTradeId(trade.getTradeId());
                    response.setSellerWalletId(trade.getSellerWalletId());
                    response.setTradeType(
                            BootCoinTradeResponse.TradeTypeEnum.valueOf(trade.getTradeType().name()));
                    response.setAmountBTC(
                            trade.getAmountBTC() != null ? trade.getAmountBTC().floatValue() : null);
                    response.setPricePEN(
                            trade.getPricePEN() != null ? trade.getPricePEN().floatValue() : null);
                    response.setTotalAmount(response.getAmountBTC() * response.getPricePEN() );
                    response.setStatus(BootCoinTradeResponse.StatusEnum.valueOf(trade.getStatus().name()));
                    response.setCreatedAt(
                            OffsetDateTime.of(trade.getCreatedAt(), ZoneOffset.UTC));

                    return ResponseEntity.status(201).body(response);
                })

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
                .map(trade -> {
                    BootCoinTradeAcceptResponse response = new BootCoinTradeAcceptResponse();
                    response.setTradeId(trade.getTradeId());
                    response.setBuyerWalletId(trade.getBuyerWalletId());
                    response.setSellerWalletId(trade.getSellerWalletId());
                    response.setAmountBTC(
                            trade.getAmountBTC() != null ? trade.getAmountBTC().floatValue() : null);
                    response.setPricePEN(
                            trade.getPricePEN() != null ? trade.getPricePEN().floatValue() : null);
                    response.setStatus(trade.getStatus().name());
                    response.setCompletedAt(
                            trade.getCompletedAt() != null
                                    ? OffsetDateTime.of(trade.getCompletedAt(), ZoneOffset.UTC)
                                    : null);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

}
