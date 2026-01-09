package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinTradeUseCase;
import com.bca.bootcoin_service.domain.ports.output.BootCoinTradeRepository;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateBootCoinTradeUseCaseImpl implements CreateBootCoinTradeUseCase {

    private final BootCoinTradeRepository tradeRepository;
    private final BootCoinWalletRepository walletRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateBootCoinTradeUseCaseImpl.class);

    public CreateBootCoinTradeUseCaseImpl(BootCoinTradeRepository tradeRepository,
                                          BootCoinWalletRepository walletRepository) {
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<BootCoinTrade> createTrade(String walletId, BootCoinTrade.TradeType tradeType,
                                          BigDecimal amountBTC, BigDecimal pricePEN) {
        logger.info("createTrade called walletId={} tradeType={} amountBTC={} pricePEN={}", walletId, tradeType, amountBTC, pricePEN);
        if (walletId == null || walletId.isBlank()) {
            return Mono.error(new IllegalArgumentException("walletId is required"));
        }
        if (tradeType == null) {
            return Mono.error(new IllegalArgumentException("tradeType is required"));
        }
        if (amountBTC == null || amountBTC.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("amountBTC must be positive"));
        }
        if (pricePEN == null || pricePEN.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("pricePEN must be positive"));
        }

        return walletRepository.findById(walletId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Wallet not found: " + walletId)))
            .flatMap(wallet -> {
                /* if (tradeType == BootCoinTrade.TradeType.SELL && wallet.getBalanceBTC().compareTo(amountBTC) < 0) {
                    logger.warn("Insufficient balance for sell trade walletId={} required={} available={}", walletId, amountBTC, wallet.getBalanceBTC());
                    return Mono.error(new IllegalArgumentException("Insufficient balance for sell trade"));
                } */

                String tradeId = "trade-" + UUID.randomUUID().toString().substring(0, 3);
                BootCoinTrade trade = new BootCoinTrade();
                trade.setTradeId(tradeId);
                trade.setSellerWalletId(walletId);
                trade.setTradeType(tradeType);
                trade.setAmountBTC(amountBTC);
                trade.setPricePEN(pricePEN);
                trade.setTotalAmount(amountBTC.multiply(pricePEN));
                trade.setStatus(BootCoinTrade.TradeStatus.OPEN);
                trade.setCreatedAt(LocalDateTime.now());

                logger.info("Persisting trade id={} walletId={} type={}", tradeId, walletId, tradeType);
                return tradeRepository.save(trade);
            });
    }
}