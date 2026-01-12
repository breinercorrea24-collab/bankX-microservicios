package com.bca.wallets_service.infrastructure.output.persistence;

import com.bca.wallets_service.domain.model.WalletDebitLink;
import com.bca.wallets_service.domain.ports.output.WalletDebitLinkRepository;
import com.bca.wallets_service.infrastructure.output.persistence.mapper.WalletDebitLinkMapper;
import com.bca.wallets_service.infrastructure.output.persistence.repository.WalletDebitLinkMongoRepository;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Repository;

@Repository
public class WalletDebitLinkRepositoryImpl implements WalletDebitLinkRepository {

    private final WalletDebitLinkMongoRepository walletDebitLinkRepository;

    public WalletDebitLinkRepositoryImpl(WalletDebitLinkMongoRepository walletDebitLinkRepository) {
        this.walletDebitLinkRepository = walletDebitLinkRepository;
    }

    @Override
    public Mono<WalletDebitLink> save(WalletDebitLink link) {

        return walletDebitLinkRepository.save(WalletDebitLinkMapper.toDocument(link))
                   .map(savedEntity -> WalletDebitLinkMapper.toDomain(savedEntity));
    }

    @Override
    public Mono<WalletDebitLink> findByWalletId(String walletId) {
        return walletDebitLinkRepository.findByWalletId(walletId)
                   .map(WalletDebitLinkMapper::toDomain);
    }
}