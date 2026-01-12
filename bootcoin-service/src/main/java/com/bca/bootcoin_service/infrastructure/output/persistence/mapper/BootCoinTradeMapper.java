package com.bca.bootcoin_service.infrastructure.output.persistence.mapper;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinTradeDocument;
import com.mongodb.lang.NonNull;

public class BootCoinTradeMapper {

    public static BootCoinTrade toDomain(@NonNull BootCoinTradeDocument document) {
        BootCoinTrade trade = new BootCoinTrade();
        trade.setTradeId(document.getTradeId());
        trade.setTradeType(BootCoinTrade.TradeType.valueOf(document.getTradeType()));
        trade.setAmountBTC(document.getAmountBTC());
        trade.setPricePEN(document.getPricePEN());
        trade.setStatus(BootCoinTrade.TradeStatus.valueOf(document.getStatus()));
        trade.setCreatedAt(document.getCreatedAt());
        trade.setCompletedAt(document.getCompletedAt());
        trade.setBuyerWalletId(document.getBuyerWalletId());
        trade.setSellerWalletId(document.getSellerWalletId());
        return trade;
    }

    public static BootCoinTradeDocument toDocument(BootCoinTrade trade) {
        return new BootCoinTradeDocument(
            trade.getTradeId(),
            trade.getTradeType().name(),
            trade.getAmountBTC(),
            trade.getPricePEN(),
            trade.getTotalAmount(),
            trade.getStatus().name(),
            trade.getCreatedAt(),
            trade.getCompletedAt(),
            trade.getBuyerWalletId(),
            trade.getSellerWalletId()
        );
    }
}
