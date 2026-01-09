package com.bca.bootcoin_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface BootCoinWalletMongoRepository extends ReactiveMongoRepository<BootCoinWalletDocument, String> {

    Mono<BootCoinWalletDocument> findByCustomerId(String customerId);

    @Query("{ 'walletId' : ?0 }")
    Mono<BootCoinWalletDocument> findByWalletId(String walletId);

}
