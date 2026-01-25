package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.output.BootCoinTradeRepository;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBootCoinTradeUseCaseImplTest {

    @Mock
    BootCoinTradeRepository tradeRepository;

    @Mock
    BootCoinWalletRepository walletRepository;

    CreateBootCoinTradeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBootCoinTradeUseCaseImpl(tradeRepository, walletRepository);
    }

    @Test
    void shouldRejectMissingWalletId() {
        StepVerifier.create(useCase.createTrade(null, BootCoinTrade.TradeType.SELL, BigDecimal.ONE, BigDecimal.ONE))
            .expectErrorMessage("walletId is required")
            .verify();

        StepVerifier.create(useCase.createTrade("", BootCoinTrade.TradeType.SELL, BigDecimal.ONE, BigDecimal.ONE))
            .expectErrorMessage("walletId is required")
            .verify();
    }

    @Test
    void shouldRejectMissingTradeType() {
        StepVerifier.create(useCase.createTrade("wallet", null, BigDecimal.ONE, BigDecimal.ONE))
            .expectErrorMessage("tradeType is required")
            .verify();
    }

    @Test
    void shouldRejectNonPositiveAmount() {
        StepVerifier.create(useCase.createTrade("wallet", BootCoinTrade.TradeType.BUY, BigDecimal.ZERO, BigDecimal.ONE))
            .expectErrorMessage("amountBTC must be positive")
            .verify();
    }

    @Test
    void shouldRejectNonPositivePrice() {
        StepVerifier.create(useCase.createTrade("wallet", BootCoinTrade.TradeType.BUY, BigDecimal.ONE, BigDecimal.ZERO))
            .expectErrorMessage("pricePEN must be positive")
            .verify();
    }

    @Test
    void shouldRejectMissingWallet() {
        when(walletRepository.findById("wallet-1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createTrade("wallet-1", BootCoinTrade.TradeType.SELL, BigDecimal.ONE, BigDecimal.ONE))
            .expectErrorMessage("Wallet not found: wallet-1")
            .verify();
    }

    @Test
    void shouldCreateTradeWhenWalletExists() {
        var wallet = new BootCoinWallet("wallet-1", "customer", "doc", BigDecimal.TEN, BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());
        when(walletRepository.findById("wallet-1")).thenReturn(Mono.just(wallet));
        when(tradeRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createTrade("wallet-1", BootCoinTrade.TradeType.SELL, new BigDecimal("2"), new BigDecimal("1000")))
            .assertNext(trade -> {
                assertEquals("wallet-1", trade.getSellerWalletId());
                assertEquals(BootCoinTrade.TradeStatus.OPEN, trade.getStatus());
                assertEquals(new BigDecimal("2000"), trade.getTotalAmount());
                assertTrue(trade.getTradeId().startsWith("trade-"));
            })
            .verifyComplete();
    }
}
