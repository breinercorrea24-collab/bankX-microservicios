package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.ports.input.AcceptBootCoinTradeUseCase;
import com.bca.bootcoin_service.domain.ports.output.BootCoinTradeRepository;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AcceptBootCoinTradeUseCaseImpl implements AcceptBootCoinTradeUseCase {

    private final BootCoinTradeRepository tradeRepository;
    private final BootCoinWalletRepository walletRepository;
    private static final Logger logger = LoggerFactory.getLogger(AcceptBootCoinTradeUseCaseImpl.class);

    public AcceptBootCoinTradeUseCaseImpl(BootCoinTradeRepository tradeRepository,
                                         BootCoinWalletRepository walletRepository) {
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<BootCoinTrade> acceptTrade(String tradeId, String buyerWalletId) {
        logger.info("acceptTrade called tradeId={} buyerWalletId={}", tradeId, buyerWalletId);
        if (tradeId == null || tradeId.isBlank()) {
            return Mono.error(new IllegalArgumentException("tradeId is required"));
        }
        if (buyerWalletId == null || buyerWalletId.isBlank()) {
            return Mono.error(new IllegalArgumentException("buyerWalletId is required"));
        }

        return tradeRepository.findById(tradeId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Trade not found: " + tradeId)))
            .flatMap(trade -> {
                if (trade.getStatus() != BootCoinTrade.TradeStatus.OPEN) {
                    logger.warn("Trade {} not open for acceptance. Current status={}", tradeId, trade.getStatus());
                    return Mono.error(new IllegalArgumentException("Trade is not available for acceptance"));
                }

                logger.info("Looking up buyer wallet {} and seller wallet {}", buyerWalletId, trade.getSellerWalletId());
                return walletRepository.findById(buyerWalletId)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Buyer wallet not found: " + buyerWalletId)))
                    .zipWith(walletRepository.findById(trade.getSellerWalletId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Seller wallet not found: " + trade.getSellerWalletId()))))
                    .flatMap(tuple -> {
                        var buyerWallet = tuple.getT1();
                        var sellerWallet = tuple.getT2();
                        if (trade.getTradeType() == BootCoinTrade.TradeType.SELL) {
                            if (sellerWallet.getBalanceBTC().compareTo(trade.getAmountBTC()) < 0) {
                                logger.warn("Seller {} has insufficient BTC. required={} available={}", sellerWallet.getWalletId(), trade.getAmountBTC(), sellerWallet.getBalanceBTC());
                                return Mono.error(new IllegalArgumentException("Seller has insufficient BTC balance"));
                            }
                            BigDecimal newSellerBalance = sellerWallet.getBalanceBTC().subtract(trade.getAmountBTC());
                            BigDecimal newBuyerBalance = buyerWallet.getBalanceBTC().add(trade.getAmountBTC());
                            logger.info("Updating balances for SELL trade {}: seller={} buyer={}", tradeId, sellerWallet.getWalletId(), buyerWallet.getWalletId());
                            return walletRepository.updateBalance(sellerWallet.getWalletId(), newSellerBalance)
                                .then(walletRepository.updateBalance(buyerWallet.getWalletId(), newBuyerBalance))
                                .then(updateTradeStatus(trade, buyerWalletId));
                        } else if (trade.getTradeType() == BootCoinTrade.TradeType.BUY) {
                            if (buyerWallet.getBalanceBTC().compareTo(trade.getAmountBTC()) < 0) {
                                logger.warn("Buyer {} has insufficient BTC. required={} available={}", buyerWallet.getWalletId(), trade.getAmountBTC(), buyerWallet.getBalanceBTC());
                                return Mono.error(new IllegalArgumentException("Buyer has insufficient BTC balance"));
                            }
                            BigDecimal newBuyerBalance = buyerWallet.getBalanceBTC().subtract(trade.getAmountBTC());
                            BigDecimal newSellerBalance = sellerWallet.getBalanceBTC().add(trade.getAmountBTC());
                            logger.info("Updating balances for BUY trade {}: buyer={} seller={}", tradeId, buyerWallet.getWalletId(), sellerWallet.getWalletId());
                            return walletRepository.updateBalance(buyerWallet.getWalletId(), newBuyerBalance)
                                .then(walletRepository.updateBalance(sellerWallet.getWalletId(), newSellerBalance))
                                .then(updateTradeStatus(trade, buyerWalletId));
                        }
                        return Mono.error(new IllegalArgumentException("Invalid trade type"));
                    });
            });
    }

    private Mono<BootCoinTrade> updateTradeStatus(BootCoinTrade trade, String buyerWalletId) {
        logger.info("Marking trade {} as COMPLETED by buyer {}", trade.getTradeId(), buyerWalletId);
        trade.setStatus(BootCoinTrade.TradeStatus.COMPLETED);
        trade.setCompletedAt(LocalDateTime.now());
        trade.setBuyerWalletId(buyerWalletId);
        trade.setSellerWalletId(trade.getSellerWalletId());

        return tradeRepository.save(trade)
            .doOnSuccess(t -> logger.info("Trade {} status updated and saved", t.getTradeId()));
    }
}