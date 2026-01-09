package com.bca.bootcoin_service.domain.ports.input;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import reactor.core.publisher.Mono;

public interface AcceptBootCoinTradeUseCase {
    Mono<BootCoinTrade> acceptTrade(String tradeId, String buyerWalletId);
}