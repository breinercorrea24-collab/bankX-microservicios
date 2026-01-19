package com.bca.cards_service.domain.model.card;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Card {

    
    protected final String id;
    protected final String cardNumber;
    protected CardStatus status;
    protected CardType type;


    protected String customerId;
    protected String maskedNumber;
    protected BigDecimal availableLimit;
    protected LocalDateTime createdAt;


    public CardStatus getStatus() {
        return status;
    }


    public boolean isActive() {
        return this.status == CardStatus.ACTIVE;
    }

}


      



