package com.bca.cards_service.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public record Card(
    CardId id,
    CardType type,
    CustomerId customerId,
    String maskedNumber,
    CardStatus status,
    BigDecimal creditLimit,
    BigDecimal availableLimit,
    AccountId linkedAccountId,
    LocalDateTime createdAt
) {
    public enum CardType {
        DEBIT, CREDIT
    }

    public enum CardStatus {
        ACTIVE, INACTIVE
    }

    public static Card createDebitCard(CardId id, CustomerId customerId, String maskedNumber) {
        Card card = new Card(id, CardType.DEBIT, customerId, maskedNumber, CardStatus.ACTIVE,
                           null, null, null, LocalDateTime.now());
        log.info("Created debit card: {} for customer: {}", id.value(), customerId.value());
        return card;
    }

    public static Card createCreditCard(CardId id, CustomerId customerId, String maskedNumber,
                                       BigDecimal creditLimit) {
        Card card = new Card(id, CardType.CREDIT, customerId, maskedNumber, CardStatus.ACTIVE,
                           creditLimit, creditLimit, null, LocalDateTime.now());
        log.info("Created credit card: {} for customer: {} with limit: {}", id.value(), customerId.value(), creditLimit);
        return card;
    }

    public Card linkToAccount(AccountId accountId) {
        if (this.type != CardType.DEBIT) {
            log.error("Attempted to link non-debit card {} to account {}", id.value(), accountId.value());
            throw new IllegalStateException("Only debit cards can be linked to accounts");
        }
        Card linkedCard = new Card(id, type, customerId, maskedNumber, status, creditLimit, availableLimit,
                                 accountId, createdAt);
        log.info("Linked debit card {} to account {}", id.value(), accountId.value());
        return linkedCard;
    }

    public boolean isDebit() {
        return type == CardType.DEBIT;
    }

    public boolean isCredit() {
        return type == CardType.CREDIT;
    }
}