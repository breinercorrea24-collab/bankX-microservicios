package com.bca.bootcoin_service.infrastructure.output.persistence;

import org.springframework.stereotype.Component;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BootCoinWalletRepositoryAdapter implements BootCoinWalletRepository {

    private final BootCoinWalletMongoRepository mongoRepository;

    public BootCoinWalletRepositoryAdapter(BootCoinWalletMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Mono<BootCoinWallet> save(BootCoinWallet wallet) {
        log.info("Saving wallet with ID: {}", wallet.getWalletId());

        BootCoinWalletDocument document = new BootCoinWalletDocument(
            wallet.getWalletId(),
            wallet.getCustomerId(),
            wallet.getDocument(),
            wallet.getBalanceBTC(),
            wallet.getStatus().name(),
            wallet.getCreatedAt()
        );

        return mongoRepository.save(document)
                .flatMap(savedDocument -> {
                    if (savedDocument == null) {
                        log.error("Failed to save wallet with ID: {}", wallet.getWalletId());
                        return Mono.error(new RuntimeException("Failed to save wallet"));
                    }
                    return Mono.just(toDomain(savedDocument));
                });
    }

    @Override
    public Mono<BootCoinWallet> findById(String walletId) {
        log.info("Finding wallet by ID: {}", walletId);

        return mongoRepository.findByWalletId(walletId)
                .map(this::toDomain);
    }

    @Override
    public Mono<BootCoinWallet> findByCustomerId(String customerId) {
        log.info("Finding wallet by customer ID: {}", customerId);

        return mongoRepository.findByCustomerId(customerId)
                .map(this::toDomain);
    }

    @Override
    public Mono<BootCoinWallet> updateBalance(String walletId, java.math.BigDecimal newBalance) {
        log.info("Updating balance for wallet ID: {} to {}", walletId, newBalance);

        return mongoRepository.findByWalletId(walletId)
                .flatMap(document -> {
                    if (document != null) {
                        document.setBalanceBTC(newBalance);
                        return mongoRepository.save(document)
                                .flatMap(savedDocument -> {
                                    if (savedDocument == null) {
                                        log.error("Failed to update balance for wallet ID: {}", walletId);
                                        return Mono.error(new RuntimeException("Failed to update wallet balance"));
                                    }
                                    return Mono.just(toDomain(savedDocument));
                                });
                    } else {
                        log.warn("Wallet not found for balance update: {}", walletId);
                        return Mono.empty();
                    }
                });
    }

    private BootCoinWallet toDomain(BootCoinWalletDocument document) {
        BootCoinWallet wallet = new BootCoinWallet();
        wallet.setWalletId(document.getWalletId());
        wallet.setCustomerId(document.getCustomerId());
        wallet.setDocument(document.getDocument());
        wallet.setBalanceBTC(document.getBalanceBTC());
        wallet.setStatus(BootCoinWallet.WalletStatus.valueOf(document.getStatus()));
        wallet.setCreatedAt(document.getCreatedAt());
        return wallet;
    }

}
