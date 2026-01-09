package com.bca.wallets_service.infrastructure.output.persistence;

import org.springframework.stereotype.Component;

import com.bca.wallets_service.domain.model.YankiWallet;
import com.bca.wallets_service.domain.ports.output.YankiWalletRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class YankiWalletRepositoryAdapter implements YankiWalletRepository {

    private final YankiWalletMongoRepository mongoRepository;

    public YankiWalletRepositoryAdapter(YankiWalletMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Mono<YankiWallet> save(YankiWallet wallet) {
        YankiWalletDocument document = new YankiWalletDocument();
        document.setId(null); // id will be generated
        document.setWalletId(wallet.getWalletId());
        document.setCustomerId(wallet.getCustomerId());
        document.setPhone(wallet.getPhone());
        document.setBalance(wallet.getBalance());
        document.setCurrency(wallet.getCurrency());
        document.setStatus(wallet.getStatus().name());
        document.setCreatedAt(wallet.getCreatedAt());

        return mongoRepository.save(document)
            .map(saved -> new YankiWallet(
                saved.getWalletId(),
                saved.getCustomerId(),
                saved.getPhone(),
                saved.getBalance(),
                saved.getCurrency(),
                YankiWallet.WalletStatus.valueOf(saved.getStatus()),
                saved.getCreatedAt()
            ))
            .doOnError(error -> log.error("Error saving wallet: {}", error.getMessage()));
    }

    @Override
    public Mono<YankiWallet> findById(String walletId) {
        return mongoRepository.findByWalletId(walletId)
            .map(document -> new YankiWallet(
                document.getWalletId(),
                document.getCustomerId(),
                document.getPhone(),
                document.getBalance(),
                document.getCurrency(),
                YankiWallet.WalletStatus.valueOf(document.getStatus()),
                document.getCreatedAt()
            ));
    }

    @Override
    public Mono<YankiWallet> findByPhone(String phone) {
        return mongoRepository.findByPhone(phone)
            .map(document -> new YankiWallet(
                document.getWalletId(),
                document.getCustomerId(),
                document.getPhone(),
                document.getBalance(),
                document.getCurrency(),
                YankiWallet.WalletStatus.valueOf(document.getStatus()),
                document.getCreatedAt()
            ));
    }

    @Override
    public Mono<YankiWallet> findByCustomerId(String customerId) {
        return mongoRepository.findByCustomerId(customerId)
            .map(document -> new YankiWallet(
                document.getWalletId(),
                document.getCustomerId(),
                document.getPhone(),
                document.getBalance(),
                document.getCurrency(),
                YankiWallet.WalletStatus.valueOf(document.getStatus()),
                document.getCreatedAt()
            ));
    }
}
