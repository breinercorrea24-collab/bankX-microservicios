package com.bca.cards_service.application.usecases.factory;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardFactoryTest {

    @Test
    void createsDebitCard() {
        CreateCardCommand cmd = new CreateCardCommand("cust-1", "acc-1", CardType.DEBIT, BigDecimal.ZERO);

        Card card = CardFactory.create(cmd);

        assertInstanceOf(DebitCard.class, card);
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        assertEquals(CardType.DEBIT, card.getType());
        assertNotNull(card.getId());
        assertNotNull(card.getMaskedNumber());
    }

    @Test
    void createsCreditCard() {
        CreateCardCommand cmd = new CreateCardCommand("cust-2", null, CardType.CREDIT, new BigDecimal("100.00"));

        Card card = CardFactory.create(cmd);

        assertInstanceOf(CreditCard.class, card);
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        assertEquals(CardType.CREDIT, card.getType());
        assertEquals(new BigDecimal("100.00"), ((CreditCard) card).getAvailableCredit());
    }

    @Test
    void failsOnUnsupportedType() {
        CreateCardCommand cmd = new CreateCardCommand("cust-3", null, null, BigDecimal.ONE);

        assertThrows(NullPointerException.class, () -> CardFactory.create(cmd));
    }
}
