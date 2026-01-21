package com.bca.cards_service.domain.model.card;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardTest {

    private static class TestCard extends Card {
        TestCard(String id, CardStatus status) {
            super(id, "card-number", status, CardType.DEBIT, "customer-1",
                    "**** **** **** 1234", BigDecimal.TEN, LocalDateTime.now());
        }
    }

    @Test
    void isActiveReturnsTrueWhenStatusIsActive() {
        assertTrue(new TestCard("id-1", CardStatus.ACTIVE).isActive());
    }

    @Test
    void isActiveReturnsFalseWhenStatusIsInactive() {
        assertFalse(new TestCard("id-2", CardStatus.BLOCKED).isActive());
    }

    @Test
    void getStatusExposesCurrentStatus() {
        assertEquals(CardStatus.ACTIVE, new TestCard("id-3", CardStatus.ACTIVE).getStatus());
    }
}
