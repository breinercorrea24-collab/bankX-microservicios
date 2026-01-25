package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.output.BootCoinTradeRepository;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptBootCoinTradeUseCaseImplTest {

    @Mock
    BootCoinTradeRepository tradeRepository;

    @Mock
    BootCoinWalletRepository walletRepository;

    AcceptBootCoinTradeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AcceptBootCoinTradeUseCaseImpl(tradeRepository, walletRepository);
    }

    @Test
    void shouldRejectWhenTradeIdMissing() {
        StepVerifier.create(useCase.acceptTrade(null, "buyer"))
            .expectErrorMessage("tradeId is required")
            .verify();

        StepVerifier.create(useCase.acceptTrade("", "buyer"))
            .expectErrorMessage("tradeId is required")
            .verify();

        verifyNoInteractions(tradeRepository, walletRepository);
    }

    @Test
    void shouldRejectWhenBuyerWalletIdMissing() {
        StepVerifier.create(useCase.acceptTrade("trade-1", null))
            .expectErrorMessage("buyerWalletId is required")
            .verify();

        StepVerifier.create(useCase.acceptTrade("trade-1", ""))
            .expectErrorMessage("buyerWalletId is required")
            .verify();

        verifyNoInteractions(tradeRepository, walletRepository);
    }

    @Test
    void shouldRejectWhenTradeNotFound() {
        when(tradeRepository.findById("trade-1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .expectErrorMessage("Trade not found: trade-1")
            .verify();

        verify(tradeRepository).findById("trade-1");
        verifyNoMoreInteractions(tradeRepository);
        verifyNoInteractions(walletRepository);
    }

    @Test
    void shouldRejectWhenTradeNotOpen() {
        var trade = buildTrade("trade-1", BootCoinTrade.TradeType.SELL, BootCoinTrade.TradeStatus.COMPLETED, "seller-wallet");
        when(tradeRepository.findById("trade-1")).thenReturn(Mono.just(trade));

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .expectErrorMessage("Trade is not available for acceptance")
            .verify();

        verify(tradeRepository).findById("trade-1");
        verifyNoInteractions(walletRepository);
    }

    @Test
    void shouldRejectWhenSellerHasInsufficientBalanceForSell() {
        var trade = buildTrade("trade-1", BootCoinTrade.TradeType.SELL, BootCoinTrade.TradeStatus.OPEN, "seller-wallet");
        var buyerWallet = buildWallet("buyer-wallet", BigDecimal.ZERO);
        var sellerWallet = buildWallet("seller-wallet", new BigDecimal("0.5"));

        when(tradeRepository.findById("trade-1")).thenReturn(Mono.just(trade));
        when(walletRepository.findById("buyer-wallet")).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.findById("seller-wallet")).thenReturn(Mono.just(sellerWallet));

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .expectErrorMessage("Seller has insufficient BTC balance")
            .verify();

        verify(tradeRepository).findById("trade-1");
        verify(walletRepository).findById("buyer-wallet");
        verify(walletRepository).findById("seller-wallet");
        verify(walletRepository, never()).updateBalance(any(), any());
    }

    @Test
    void shouldRejectWhenBuyerHasInsufficientBalanceForBuy() {
        var trade = buildTrade("trade-1", BootCoinTrade.TradeType.BUY, BootCoinTrade.TradeStatus.OPEN, "seller-wallet");
        var buyerWallet = buildWallet("buyer-wallet", new BigDecimal("0.5"));
        var sellerWallet = buildWallet("seller-wallet", BigDecimal.ONE);

        when(tradeRepository.findById("trade-1")).thenReturn(Mono.just(trade));
        when(walletRepository.findById("buyer-wallet")).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.findById("seller-wallet")).thenReturn(Mono.just(sellerWallet));

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .expectErrorMessage("Buyer has insufficient BTC balance")
            .verify();

        verify(walletRepository, never()).updateBalance(any(), any());
    }

    @Test
    void shouldRejectWhenTradeTypeInvalid() {
        var trade = buildTrade("trade-1", null, BootCoinTrade.TradeStatus.OPEN, "seller-wallet");
        var buyerWallet = buildWallet("buyer-wallet", BigDecimal.ONE);
        var sellerWallet = buildWallet("seller-wallet", BigDecimal.ONE);

        when(tradeRepository.findById("trade-1")).thenReturn(Mono.just(trade));
        when(walletRepository.findById("buyer-wallet")).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.findById("seller-wallet")).thenReturn(Mono.just(sellerWallet));

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .expectErrorMessage("Invalid trade type")
            .verify();

        verify(tradeRepository).findById("trade-1");
        verify(walletRepository).findById("buyer-wallet");
        verify(walletRepository).findById("seller-wallet");
        verify(tradeRepository, never()).save(any());
    }

    @Test
    void shouldAcceptSellTrade() {
        var amount = new BigDecimal("1.5");
        var trade = buildTrade("trade-1", BootCoinTrade.TradeType.SELL, BootCoinTrade.TradeStatus.OPEN, "seller-wallet");
        trade.setAmountBTC(amount);
        var buyerWallet = buildWallet("buyer-wallet", BigDecimal.ONE);
        var sellerWallet = buildWallet("seller-wallet", new BigDecimal("5"));

        when(tradeRepository.findById("trade-1")).thenReturn(Mono.just(trade));
        when(walletRepository.findById("buyer-wallet")).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.findById("seller-wallet")).thenReturn(Mono.just(sellerWallet));

        var expectedSellerBalance = sellerWallet.getBalanceBTC().subtract(amount);
        var expectedBuyerBalance = buyerWallet.getBalanceBTC().add(amount);

        when(walletRepository.updateBalance(eq("seller-wallet"), eq(expectedSellerBalance))).thenReturn(Mono.just(sellerWallet));
        when(walletRepository.updateBalance(eq("buyer-wallet"), eq(expectedBuyerBalance))).thenReturn(Mono.just(buyerWallet));
        when(tradeRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.acceptTrade("trade-1", "buyer-wallet"))
            .assertNext(result -> {
                assertEquals(BootCoinTrade.TradeStatus.COMPLETED, result.getStatus());
                assertEquals("buyer-wallet", result.getBuyerWalletId());
                assertNotNull(result.getCompletedAt());
            })
            .verifyComplete();

        InOrder inOrder = inOrder(walletRepository, tradeRepository);
        inOrder.verify(walletRepository).updateBalance("seller-wallet", expectedSellerBalance);
        inOrder.verify(walletRepository).updateBalance("buyer-wallet", expectedBuyerBalance);
        inOrder.verify(tradeRepository).save(trade);
    }

    @Test
    void shouldAcceptBuyTrade() {
        var amount = new BigDecimal("2");
        var trade = buildTrade("trade-2", BootCoinTrade.TradeType.BUY, BootCoinTrade.TradeStatus.OPEN, "seller-wallet");
        trade.setAmountBTC(amount);
        var buyerWallet = buildWallet("buyer-wallet", new BigDecimal("3"));
        var sellerWallet = buildWallet("seller-wallet", BigDecimal.ONE);

        when(tradeRepository.findById("trade-2")).thenReturn(Mono.just(trade));
        when(walletRepository.findById("buyer-wallet")).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.findById("seller-wallet")).thenReturn(Mono.just(sellerWallet));

        var expectedBuyerBalance = buyerWallet.getBalanceBTC().subtract(amount);
        var expectedSellerBalance = sellerWallet.getBalanceBTC().add(amount);

        when(walletRepository.updateBalance(eq("buyer-wallet"), eq(expectedBuyerBalance))).thenReturn(Mono.just(buyerWallet));
        when(walletRepository.updateBalance(eq("seller-wallet"), eq(expectedSellerBalance))).thenReturn(Mono.just(sellerWallet));
        when(tradeRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.acceptTrade("trade-2", "buyer-wallet"))
            .assertNext(result -> {
                assertEquals(BootCoinTrade.TradeStatus.COMPLETED, result.getStatus());
                assertEquals("buyer-wallet", result.getBuyerWalletId());
                assertNotNull(result.getCompletedAt());
            })
            .verifyComplete();

        InOrder inOrder = inOrder(walletRepository, tradeRepository);
        inOrder.verify(walletRepository).updateBalance("buyer-wallet", expectedBuyerBalance);
        inOrder.verify(walletRepository).updateBalance("seller-wallet", expectedSellerBalance);
        inOrder.verify(tradeRepository).save(trade);
    }

    private static BootCoinTrade buildTrade(String tradeId, BootCoinTrade.TradeType tradeType, BootCoinTrade.TradeStatus status, String sellerWalletId) {
        var trade = new BootCoinTrade();
        trade.setTradeId(tradeId);
        trade.setTradeType(tradeType);
        trade.setAmountBTC(BigDecimal.ONE);
        trade.setPricePEN(BigDecimal.TEN);
        trade.setStatus(status);
        trade.setSellerWalletId(sellerWalletId);
        trade.setCreatedAt(LocalDateTime.now());
        return trade;
    }

    private static BootCoinWallet buildWallet(String walletId, BigDecimal balance) {
        return new BootCoinWallet(walletId, "customer", "doc", balance, BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());
    }
}
