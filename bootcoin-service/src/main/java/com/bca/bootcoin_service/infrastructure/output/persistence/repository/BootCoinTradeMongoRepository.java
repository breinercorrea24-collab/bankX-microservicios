package com.bca.bootcoin_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinTradeDocument;

import reactor.core.publisher.Mono;

@Repository
public interface BootCoinTradeMongoRepository extends ReactiveMongoRepository<BootCoinTradeDocument, String> {

    Mono<BootCoinTradeDocument> findByTradeId(String tradeId);

}
