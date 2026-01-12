package com.bca.bootcoin_service.infrastructure.output.persistence;

import org.springframework.stereotype.Component;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinWalletDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinWalletMapper;
import com.bca.bootcoin_service.infrastructure.output.persistence.repository.BootCoinWalletMongoRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BootCoinWalletRepositoryAdapter implements BootCoinWalletRepository {

    private final BootCoinWalletMongoRepository mongoRepository;
    private final BootCoinWalletMapper mapper;

    public BootCoinWalletRepositoryAdapter(BootCoinWalletMongoRepository mongoRepository, BootCoinWalletMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<BootCoinWallet> save(BootCoinWallet wallet) {
        log.info("Saving wallet with ID: {}", wallet.getWalletId());

        BootCoinWalletDocument document = mapper.toDocument(wallet);

        if (document == null) {
            return Mono.empty();
        }

        return mongoRepository.save(document)
                .flatMap(savedDocument -> {
                    if (savedDocument == null) {
                        log.error("Failed to save wallet with ID: {}", wallet.getWalletId());
                        return Mono.error(new RuntimeException("Failed to save wallet"));
                    }
                    return Mono.just(mapper.toDomain(savedDocument));
                });
    }

    @Override
    public Mono<BootCoinWallet> findById(String walletId) {
        log.info("Finding wallet by ID: {}", walletId);

        return mongoRepository.findByWalletId(walletId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<BootCoinWallet> findByCustomerId(String customerId) {
        log.info("Finding wallet by customer ID: {}", customerId);

        return mongoRepository.findByCustomerId(customerId)
                .map(mapper::toDomain);
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
                                    return Mono.just(mapper.toDomain(savedDocument));
                                });
                    } else {
                        log.warn("Wallet not found for balance update: {}", walletId);
                        return Mono.empty();
                    }
                });
    }
}
