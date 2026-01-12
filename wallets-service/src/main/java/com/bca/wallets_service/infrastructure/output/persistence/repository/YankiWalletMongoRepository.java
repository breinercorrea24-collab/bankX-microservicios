package com.bca.wallets_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.wallets_service.infrastructure.output.persistence.entity.YankiWalletEntity;

import reactor.core.publisher.Mono;

@Repository
public interface YankiWalletMongoRepository extends ReactiveMongoRepository<YankiWalletEntity, String> {
    Mono<YankiWalletEntity> findByWalletId(String walletId);
    Mono<YankiWalletEntity> findByPhone(String phone);
    Mono<YankiWalletEntity> findByCustomerId(String customerId);
}
