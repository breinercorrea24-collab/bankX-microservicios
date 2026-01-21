package com.bca.cards_service.domain.model.card;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardTest {

    private CreditCard createActiveCard(BigDecimal creditLimit) {
        return new CreditCard("card-1", "411111******1111", CardStatus.ACTIVE, CardType.CREDIT,
                "customer-1", "**** **** **** 1111", creditLimit, LocalDateTime.now(), creditLimit);
    }

    @Test
    void constructorInitializesAvailableCreditEqualToLimit() {
        CreditCard card = createActiveCard(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), card.getAvailableCredit());
    }

    @Test
    void constructorRejectsNonPositiveLimit() {
        assertThrows(BusinessException.class,
                () -> createActiveCard(BigDecimal.ZERO));
    }

    @Test
    void consumeReducesAvailableCredit() {
        CreditCard card = createActiveCard(new BigDecimal("100.00"));

        card.consume(new BigDecimal("40.00"));

        assertEquals(new BigDecimal("60.00"), card.getAvailableCredit());
    }

    @Test
    void consumeFailsWhenInactive() {
        CreditCard card = new CreditCard("card-2", "4222", CardStatus.BLOCKED, CardType.CREDIT,
                "customer-2", "**** **** **** 2222", new BigDecimal("50.00"),
                LocalDateTime.now(), new BigDecimal("50.00"));

        assertThrows(BusinessException.class, () -> card.consume(new BigDecimal("10.00")));
    }

    @Test
    void consumeFailsWhenAmountInvalid() {
        CreditCard card = createActiveCard(new BigDecimal("25.00"));

        assertThrows(BusinessException.class, () -> card.consume(BigDecimal.ZERO));
    }

    @Test
    void consumeFailsWhenInsufficientCredit() {
        CreditCard card = createActiveCard(new BigDecimal("30.00"));

        assertThrows(BusinessException.class, () -> card.consume(new BigDecimal("40.00")));
    }

    @Test
    void payAddsCreditAndCapsAtLimit() {
        CreditCard card = createActiveCard(new BigDecimal("100.00"));
        card.consume(new BigDecimal("80.00"));

        card.pay(new BigDecimal("200.00"));

        assertEquals(new BigDecimal("100.00"), card.getAvailableCredit());
    }

    @Test
    void payAddsCreditWhenWithinLimit() {
        CreditCard card = createActiveCard(new BigDecimal("90.00"));
        card.consume(new BigDecimal("40.00"));

        card.pay(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("70.00"), card.getAvailableCredit());
    }

    @Test
    void payFailsWhenAmountInvalid() {
        CreditCard card = createActiveCard(new BigDecimal("60.00"));

        assertThrows(BusinessException.class, () -> card.pay(new BigDecimal("-1.00")));
    }
}
