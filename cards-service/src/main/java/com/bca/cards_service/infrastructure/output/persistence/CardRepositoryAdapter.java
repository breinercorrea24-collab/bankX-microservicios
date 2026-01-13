package com.bca.cards_service.infrastructure.output.persistence;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.ports.CardRepository;
import com.bca.cards_service.infrastructure.output.persistence.entity.CardDocument;
import com.bca.cards_service.infrastructure.output.persistence.mapper.CardsMapper;
import com.bca.cards_service.infrastructure.output.persistence.repository.CardMongoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CardRepositoryAdapter implements CardRepository {

    private final CardMongoRepository cardMongoRepository;

    @Override
    public Mono<Card> save(Card card) {
        log.debug("Saving card: {}", card.id().value());
        CardDocument document = CardsMapper.from(card);
        log.debug("Saving document card: {}", document);
        return cardMongoRepository.save(document)
                .map(CardsMapper::toDomain)
                .doOnSuccess(savedCard -> log.info("Card saved successfully: {}", savedCard.id().value()))
                .doOnError(error -> log.error("Failed to save card: {}", card.id().value(), error));
    }

    @Override
    public Mono<Card> findById(CardId cardId) {
        log.debug("Finding card by ID: {}", cardId.value());
        return cardMongoRepository.findByCardId(cardId.value())
                .map(CardsMapper::toDomain)
                .doOnSuccess(card -> log.debug("Card found: {}", cardId.value()))
                .doOnError(error -> log.error("Failed to find card: {}", cardId.value(), error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Card not found: {}", cardId.value());
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Boolean> findByCustomerId(String customerId) {
        log.debug("Finding card by Customer ID: {}", customerId);
        return cardMongoRepository.findByCustomerId(customerId)
                .map(cardDocument -> {
                    log.debug("Card found for Customer ID: {}", customerId);
                    return true;
                })
                .defaultIfEmpty(false)
                .doOnError(error -> log.error("Failed to find card for Customer ID: {}", customerId, error));
    }
}
