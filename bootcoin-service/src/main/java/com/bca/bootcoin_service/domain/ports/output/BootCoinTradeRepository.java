package com.bca.bootcoin_service.domain.ports.output;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;

import reactor.core.publisher.Mono;


public interface BootCoinTradeRepository {
    Mono<BootCoinTrade> save(BootCoinTrade trade);
    Mono<BootCoinTrade> findById(String tradeId);
    Mono<BootCoinTrade> updateStatus(String tradeId, BootCoinTrade.TradeStatus status);
}