package com.bca.bootcoin_service.domain.ports.input;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.model.BootCoinTrade.TradeType;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface CreateBootCoinTradeUseCase {
    Mono<BootCoinTrade> createTrade(String walletId, TradeType tradeType, BigDecimal amountBTC, BigDecimal pricePEN);
}