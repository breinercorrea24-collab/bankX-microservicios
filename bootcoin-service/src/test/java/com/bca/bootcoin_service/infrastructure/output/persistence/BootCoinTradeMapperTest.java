package com.bca.bootcoin_service.infrastructure.output.persistence;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinTradeDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinTradeMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BootCoinTradeMapperTest {

    @Test
    void shouldMapDocumentToDomain() {
        var document = new BootCoinTradeDocument(
            "trade-1",
            BootCoinTrade.TradeType.BUY.name(),
            new BigDecimal("1"),
            new BigDecimal("2"),
            new BigDecimal("2"),
            BootCoinTrade.TradeStatus.OPEN.name(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "buyer-id",
            "seller-id"
        );

        var domain = BootCoinTradeMapper.toDomain(document);

        assertEquals(document.getTradeId(), domain.getTradeId());
        assertEquals(BootCoinTrade.TradeStatus.OPEN, domain.getStatus());
    }

    @Test
    void shouldMapDomainToDocument() {
        var domain = new BootCoinTrade();
        domain.setTradeId("trade-2");
        domain.setTradeType(BootCoinTrade.TradeType.SELL);
        domain.setAmountBTC(new BigDecimal("3"));
        domain.setPricePEN(new BigDecimal("4"));
        domain.setTotalAmount(new BigDecimal("12"));
        domain.setStatus(BootCoinTrade.TradeStatus.COMPLETED);
        domain.setCreatedAt(LocalDateTime.now());
        domain.setCompletedAt(LocalDateTime.now());
        domain.setBuyerWalletId("buyer");
        domain.setSellerWalletId("seller");

        var document = BootCoinTradeMapper.toDocument(domain);

        assertEquals(domain.getTradeId(), document.getTradeId());
        assertEquals(BootCoinTrade.TradeType.SELL.name(), document.getTradeType());
    }
}
