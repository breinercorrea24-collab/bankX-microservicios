package com.bca.bootcoin_service.infrastructure.input.rest.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptResponse;
import com.bca.bootcoin_service.dto.BootCoinTradeRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeResponse;

@Component
public class BootCoinTradeApiMapper {

    public BootCoinTrade.TradeType toDomainTradeType(BootCoinTradeRequest.TradeTypeEnum tradeTypeEnum) {
        return BootCoinTrade.TradeType.valueOf(tradeTypeEnum.name());
    }

    public BigDecimal toBigDecimal(Float value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    public BootCoinTradeResponse toResponse(BootCoinTrade trade) {
        BootCoinTradeResponse response = new BootCoinTradeResponse();
        response.setTradeId(trade.getTradeId());
        response.setSellerWalletId(trade.getSellerWalletId());
        response.setTradeType(BootCoinTradeResponse.TradeTypeEnum.valueOf(trade.getTradeType().name()));
        response.setAmountBTC(trade.getAmountBTC() != null ? trade.getAmountBTC().floatValue() : null);
        response.setPricePEN(trade.getPricePEN() != null ? trade.getPricePEN().floatValue() : null);
        response.setTotalAmount(response.getAmountBTC() * response.getPricePEN());
        response.setStatus(BootCoinTradeResponse.StatusEnum.valueOf(trade.getStatus().name()));
        response.setCreatedAt(OffsetDateTime.of(trade.getCreatedAt(), ZoneOffset.UTC));
        return response;
    }

    public BootCoinTradeAcceptResponse toAcceptResponse(BootCoinTrade trade) {
        BootCoinTradeAcceptResponse response = new BootCoinTradeAcceptResponse();
        response.setTradeId(trade.getTradeId());
        response.setBuyerWalletId(trade.getBuyerWalletId());
        response.setSellerWalletId(trade.getSellerWalletId());
        response.setAmountBTC(trade.getAmountBTC() != null ? trade.getAmountBTC().floatValue() : null);
        response.setPricePEN(trade.getPricePEN() != null ? trade.getPricePEN().floatValue() : null);
        response.setStatus(trade.getStatus().name());
        response.setCompletedAt(trade.getCompletedAt() != null
                ? OffsetDateTime.of(trade.getCompletedAt(), ZoneOffset.UTC)
                : null);
        return response;
    }
}
