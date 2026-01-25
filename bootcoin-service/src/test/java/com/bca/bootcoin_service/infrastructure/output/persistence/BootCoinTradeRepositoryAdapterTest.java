package com.bca.bootcoin_service.infrastructure.output.persistence;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinTradeDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinTradeMapper;
import com.bca.bootcoin_service.infrastructure.output.persistence.repository.BootCoinTradeMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootCoinTradeRepositoryAdapterTest {

    @Mock
    BootCoinTradeMongoRepository mongoRepository;

    BootCoinTradeRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BootCoinTradeRepositoryAdapter(mongoRepository);
    }

    @Test
    void shouldSaveTradeAndReturnDomain() {
        var trade = buildTrade("trade-1", BootCoinTrade.TradeType.SELL, BootCoinTrade.TradeStatus.OPEN);
        var document = BootCoinTradeMapper.toDocument(trade);
        when(mongoRepository.save(any())).thenReturn(Mono.just(document));

        StepVerifier.create(adapter.save(trade))
            .assertNext(saved -> assertEquals(trade.getTradeId(), saved.getTradeId()))
            .verifyComplete();
    }

    @Test
    void shouldRaiseErrorWhenSaveFailsAtMongo() {
        var trade = buildTrade("trade-2", BootCoinTrade.TradeType.BUY, BootCoinTrade.TradeStatus.OPEN);
        when(mongoRepository.save(any())).thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(adapter.save(trade))
            .expectErrorMessage("boom")
            .verify();
    }

    @Test
    void shouldReturnEmptyWhenTradeNotFound() {
        when(mongoRepository.findByTradeId("trade-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("trade-1"))
            .verifyComplete();
    }

    @Test
    void shouldUpdateStatusWhenDocumentPresent() {
        var document = buildDocument("trade-1", BootCoinTrade.TradeType.SELL, BootCoinTrade.TradeStatus.OPEN);
        when(mongoRepository.findByTradeId("trade-1")).thenReturn(Mono.just(document));
        when(mongoRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.updateStatus("trade-1", BootCoinTrade.TradeStatus.COMPLETED))
            .assertNext(updated -> assertEquals(BootCoinTrade.TradeStatus.COMPLETED, updated.getStatus()))
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUpdatingMissingTrade() {
        when(mongoRepository.findByTradeId("trade-9")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateStatus("trade-9", BootCoinTrade.TradeStatus.COMPLETED))
            .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenUpdateSaveFails() {
        var document = buildDocument("trade-3", BootCoinTrade.TradeType.BUY, BootCoinTrade.TradeStatus.OPEN);
        when(mongoRepository.findByTradeId("trade-3")).thenReturn(Mono.just(document));
        when(mongoRepository.save(any())).thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(adapter.updateStatus("trade-3", BootCoinTrade.TradeStatus.COMPLETED))
            .expectErrorMessage("boom")
            .verify();
    }

    private static BootCoinTrade buildTrade(String tradeId, BootCoinTrade.TradeType type, BootCoinTrade.TradeStatus status) {
        var trade = new BootCoinTrade();
        trade.setTradeId(tradeId);
        trade.setTradeType(type);
        trade.setAmountBTC(new BigDecimal("1"));
        trade.setPricePEN(new BigDecimal("2"));
        trade.setTotalAmount(trade.getAmountBTC().multiply(trade.getPricePEN()));
        trade.setStatus(status);
        trade.setCreatedAt(LocalDateTime.now());
        trade.setSellerWalletId("seller-1");
        trade.setBuyerWalletId("buyer-1");
        return trade;
    }

    private static BootCoinTradeDocument buildDocument(String tradeId, BootCoinTrade.TradeType type, BootCoinTrade.TradeStatus status) {
        return BootCoinTradeMapper.toDocument(buildTrade(tradeId, type, status));
    }
}
