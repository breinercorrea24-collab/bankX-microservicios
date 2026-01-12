package com.bca.cards_service.infrastructure.output.persistence.mapper;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.infrastructure.output.persistence.entity.CardDocument;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardsMapper {
     public static CardDocument from(Card card) {
        log.debug("Converting Card domain object to CardDocument for card: {}", card.id().value());
        CardDocument document = new CardDocument(
            card.id().value(),
            card.id().value(),
            card.type().name(),
            card.customerId().value(),
            card.maskedNumber(),
            card.status().name(),
            card.creditLimit(),
            card.availableLimit(),
            card.linkedAccountId() != null ? card.linkedAccountId().value() : null,
            card.createdAt()
        );
        log.debug("CardDocument created: {}", document.cardId());
        return document;
    }

    public static Card toDomain(CardDocument cardDocument) {
        log.debug("Converting CardDocument to Card domain object for card: {}", cardDocument.cardId());
        Card card = new Card(
            new com.bca.cards_service.domain.model.CardId(cardDocument.cardId()),
            Card.CardType.valueOf(cardDocument.type()),
            new com.bca.cards_service.domain.model.CustomerId(cardDocument.customerId()),
            cardDocument.maskedNumber(),
            Card.CardStatus.valueOf(cardDocument.status()),
            cardDocument.creditLimit(),
            cardDocument.availableLimit(),
            cardDocument.linkedAccountId() != null ? new com.bca.cards_service.domain.model.AccountId(cardDocument.linkedAccountId()) : null,
            cardDocument.createdAt()
        );
        log.debug("Card domain object created: {}", card.id().value());
        return card;
    }
}
