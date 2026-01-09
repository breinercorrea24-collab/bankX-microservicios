package com.bca.cards_service.infrastructure.output.persistence;

import com.bca.cards_service.domain.model.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Document(collection = "cards")
public record CardDocument(
    @Id String id,
    String cardId,
    String type,
    String customerId,
    String maskedNumber,
    String status,
    BigDecimal creditLimit,
    BigDecimal availableLimit,
    String linkedAccountId,
    LocalDateTime createdAt
) {
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

    public Card toDomain() {
        log.debug("Converting CardDocument to Card domain object for card: {}", cardId);
        Card card = new Card(
            new com.bca.cards_service.domain.model.CardId(cardId),
            Card.CardType.valueOf(type),
            new com.bca.cards_service.domain.model.CustomerId(customerId),
            maskedNumber,
            Card.CardStatus.valueOf(status),
            creditLimit,
            availableLimit,
            linkedAccountId != null ? new com.bca.cards_service.domain.model.AccountId(linkedAccountId) : null,
            createdAt
        );
        log.debug("Card domain object created: {}", card.id().value());
        return card;
    }
}
