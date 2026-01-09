package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinWalletUseCase;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateBootCoinWalletUseCaseImpl implements CreateBootCoinWalletUseCase {

    private final BootCoinWalletRepository walletRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreateBootCoinWalletUseCaseImpl.class);

    public CreateBootCoinWalletUseCaseImpl(BootCoinWalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<BootCoinWallet> createWallet(String customerId, String document) {
        logger.info("createWallet called for customerId={} document={}", customerId, document);
        return walletRepository.findByCustomerId(customerId)
            .flatMap(existingWallet -> {
                if (existingWallet != null) {
                    logger.warn("Wallet already exists for customer: {}", customerId);
                    return Mono.error(new IllegalArgumentException("Wallet already exists for customer: " + customerId));
                }

                String walletId = "boot-" + UUID.randomUUID().toString().substring(0, 3);
                BootCoinWallet wallet = new BootCoinWallet(
                    walletId,
                    customerId,
                    document,
                    BigDecimal.ZERO,
                    BootCoinWallet.WalletStatus.ACTIVE,
                    LocalDateTime.now()
                );

                logger.info("Creating wallet id={} for customerId={}", walletId, customerId);
                return walletRepository.save(wallet);
            })
            .switchIfEmpty(Mono.defer(() -> {
                String walletId = "boot-" + UUID.randomUUID().toString().substring(0, 3);
                BootCoinWallet wallet = new BootCoinWallet(
                    walletId,
                    customerId,
                    document,
                    BigDecimal.ZERO,
                    BootCoinWallet.WalletStatus.ACTIVE,
                    LocalDateTime.now()
                );

                logger.info("Creating wallet (switchIfEmpty) id={} for customerId={}", walletId, customerId);
                return walletRepository.save(wallet);
            }));
    }
}