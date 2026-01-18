package com.bca.cards_service.domain.model.card;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;

public class CreditCard extends Card {

    private final BigDecimal creditLimit;
    private BigDecimal availableCredit;

    public CreditCard(
            String id, String cardNumber, CardStatus status, CardType type, String customerId, String maskedNumber,
            BigDecimal availableLimit, LocalDateTime createdAt,

            BigDecimal creditLimit) {
        super(id, cardNumber, status, type, customerId, maskedNumber, availableLimit, createdAt);

        if (creditLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Invalid credit limit");
        }

        this.creditLimit = creditLimit;
        this.availableCredit = creditLimit;
    }


    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void consume(BigDecimal amount) {
        validateActive();
        validateAmount(amount);

        if (availableCredit.compareTo(amount) < 0) {
            throw new BusinessException("Insufficient credit");
        }

        this.availableCredit = this.availableCredit.subtract(amount);
    }

    public void pay(BigDecimal amount) {
        validateAmount(amount);

        this.availableCredit = this.availableCredit.add(amount);

        if (this.availableCredit.compareTo(creditLimit) > 0) {
            this.availableCredit = creditLimit;
        }
    }

    private void validateActive() {
        if (!isActive()) {
            throw new BusinessException("Credit card is blocked");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Invalid amount");
        }
    }
}
