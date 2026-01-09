package com.bca.bootcoin_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface BootCoinTradeMongoRepository extends ReactiveMongoRepository<BootCoinTradeDocument, String> {

    Mono<BootCoinTradeDocument> findByTradeId(String tradeId);

}
