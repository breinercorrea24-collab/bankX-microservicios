package com.bca.bootcoin_service.infrastructure.input.rest.mapper;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptResponse;
import com.bca.bootcoin_service.dto.BootCoinTradeRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BootCoinTradeApiMapperTest {

    private BootCoinTradeApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BootCoinTradeApiMapper();
    }

    @Test
    void shouldConvertRequestTradeTypeToDomain() {
        assertEquals(BootCoinTrade.TradeType.BUY,
            mapper.toDomainTradeType(BootCoinTradeRequest.TradeTypeEnum.BUY));
        assertEquals(BootCoinTrade.TradeType.SELL,
            mapper.toDomainTradeType(BootCoinTradeRequest.TradeTypeEnum.SELL));
    }

    @Test
    void shouldMapFloatToBigDecimalWhenPresentOrNull() {
        assertEquals(BigDecimal.valueOf(2.5f), mapper.toBigDecimal(2.5f));
        assertNull(mapper.toBigDecimal(null));
    }

    @Test
    void shouldBuildResponseFromDomainTrade() {
        var trade = buildTrade();
        BootCoinTradeResponse response = mapper.toResponse(trade);

        assertEquals(trade.getTradeId(), response.getTradeId());
        assertEquals(trade.getSellerWalletId(), response.getSellerWalletId());
        assertEquals(BootCoinTradeResponse.TradeTypeEnum.SELL, response.getTradeType());
        assertEquals(trade.getAmountBTC().floatValue(), response.getAmountBTC());
        assertEquals(trade.getPricePEN().floatValue(), response.getPricePEN());
        assertEquals(response.getAmountBTC() * response.getPricePEN(), response.getTotalAmount());
        assertEquals(BootCoinTradeResponse.StatusEnum.OPEN, response.getStatus());
        assertEquals(trade.getCreatedAt().atOffset(response.getCreatedAt().getOffset()), response.getCreatedAt());
    }

    @Test
    void shouldBuildAcceptResponseWithAndWithoutCompletion() {
        var trade = buildTrade();
        trade.setBuyerWalletId("buyer");
        trade.setCompletedAt(LocalDateTime.now());
        BootCoinTradeAcceptResponse complete = mapper.toAcceptResponse(trade);

        assertEquals(trade.getTradeId(), complete.getTradeId());
        assertEquals(trade.getBuyerWalletId(), complete.getBuyerWalletId());
        assertEquals(trade.getSellerWalletId(), complete.getSellerWalletId());
        assertEquals(trade.getAmountBTC().floatValue(), complete.getAmountBTC());
        assertEquals(trade.getPricePEN().floatValue(), complete.getPricePEN());
        assertEquals(trade.getCompletedAt().atOffset(complete.getCompletedAt().getOffset()), complete.getCompletedAt());

        trade.setCompletedAt(null);
        BootCoinTradeAcceptResponse withoutCompletion = mapper.toAcceptResponse(trade);
        assertNull(withoutCompletion.getCompletedAt());
    }

    private static BootCoinTrade buildTrade() {
        var trade = new BootCoinTrade();
        trade.setTradeId("trade-1");
        trade.setTradeType(BootCoinTrade.TradeType.SELL);
        trade.setAmountBTC(new BigDecimal("1"));
        trade.setPricePEN(new BigDecimal("10"));
        trade.setStatus(BootCoinTrade.TradeStatus.OPEN);
        trade.setCreatedAt(LocalDateTime.now());
        trade.setSellerWalletId("seller");
        return trade;
    }
}
