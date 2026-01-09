package com.bca.wallets_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface YankiWalletMongoRepository extends ReactiveMongoRepository<YankiWalletDocument, String> {
    Mono<YankiWalletDocument> findByWalletId(String walletId);
    Mono<YankiWalletDocument> findByPhone(String phone);
    Mono<YankiWalletDocument> findByCustomerId(String customerId);
}
