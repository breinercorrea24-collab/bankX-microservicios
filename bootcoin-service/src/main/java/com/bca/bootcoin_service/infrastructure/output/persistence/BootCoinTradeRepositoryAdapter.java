package com.bca.bootcoin_service.infrastructure.output.persistence;

import org.springframework.stereotype.Component;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.ports.output.BootCoinTradeRepository;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinTradeDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinTradeMapper;
import com.bca.bootcoin_service.infrastructure.output.persistence.repository.BootCoinTradeMongoRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BootCoinTradeRepositoryAdapter implements BootCoinTradeRepository {

    private final BootCoinTradeMongoRepository mongoRepository;

    public BootCoinTradeRepositoryAdapter(BootCoinTradeMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Mono<BootCoinTrade> save(BootCoinTrade trade) {
        log.info("Saving trade with ID: {}", trade.getTradeId());

        BootCoinTradeDocument document = BootCoinTradeMapper.toDocument(trade);
        
        if (document == null) {
            return Mono.empty();
        }

        return mongoRepository.save(document)
                .map(BootCoinTradeMapper::toDomain)
                .doOnError(error -> log.error("Error saving user: {}", error.getMessage()));
    }

    @Override
    public Mono<BootCoinTrade> findById(String tradeId) {
        log.info("Finding trade by ID: {}", tradeId);

        return mongoRepository.findByTradeId(tradeId)
                .map(BootCoinTradeMapper::toDomain);
    }

    @Override
    public Mono<BootCoinTrade> updateStatus(String tradeId, BootCoinTrade.TradeStatus status) {
        log.info("Updating status for trade ID: {} to {}", tradeId, status);

        return mongoRepository.findByTradeId(tradeId)
                .flatMap(document -> {
                    if (document != null) {
                        document.setStatus(status.name());
                        return mongoRepository.save(document)
                                .flatMap(savedDocument -> {
                                    if (savedDocument == null) {
                                        log.error("Failed to update status for trade ID: {}", tradeId);
                                        return Mono.error(new RuntimeException("Failed to update trade status"));
                                    }
                                    return Mono.just(BootCoinTradeMapper.toDomain(savedDocument));
                                });
                    } else {
                        log.warn("Trade not found for status update: {}", tradeId);
                        return Mono.empty();
                    }
                });
    }
}
