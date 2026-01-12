package com.bca.wallets_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.wallets_service.infrastructure.output.persistence.entity.WalletDebitLinkEntity;

import reactor.core.publisher.Mono;

@Repository
public interface WalletDebitLinkMongoRepository extends ReactiveMongoRepository<WalletDebitLinkEntity, String>{
    Mono<WalletDebitLinkEntity> findByWalletId(String walletId);
}
