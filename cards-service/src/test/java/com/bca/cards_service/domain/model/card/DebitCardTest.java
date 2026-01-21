package com.bca.cards_service.domain.model.card;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebitCardTest {

    @Test
    void constructorRequiresPrimaryAccount() {
        assertThrows(BusinessException.class,
                () -> new DebitCard("card-1", "4111", CardStatus.ACTIVE, CardType.DEBIT,
                        "customer-1", "****", BigDecimal.ZERO, LocalDateTime.now(),
                        null, Set.of("acc-2")));
    }

    @Test
    void linkedAccountsDefaultToEmptyWhenNull() {
        DebitCard card = new DebitCard("card-2", "4222", CardStatus.ACTIVE, CardType.DEBIT,
                "customer-2", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", null);

        assertTrue(card.getLinkedAccountIds().isEmpty());
    }

    @Test
    void withdrawalOrderStartsWithPrimaryAndIsUnmodifiable() {
        Set<String> linked = new LinkedHashSet<>(List.of("acc-2", "acc-3"));
        DebitCard card = new DebitCard("card-3", "4333", CardStatus.ACTIVE, CardType.DEBIT,
                "customer-3", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", linked);

        List<String> order = card.getWithdrawalOrder();

        assertEquals(List.of("acc-1", "acc-2", "acc-3"), order);
        assertThrows(UnsupportedOperationException.class, () -> order.add("acc-4"));
    }

    @Test
    void debitOrderIsMutableAndPreservesOrder() {
        Set<String> linked = new LinkedHashSet<>(List.of("acc-2", "acc-3"));
        DebitCard card = new DebitCard("card-4", "4444", CardStatus.ACTIVE, CardType.DEBIT,
                "customer-4", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", linked);

        List<String> order = card.getDebitOrder();
        order.add("acc-4");

        assertEquals(List.of("acc-1", "acc-2", "acc-3", "acc-4"), order);
    }

    @Test
    void accountLinkedChecksPrimaryAndLinkedAccounts() {
        Set<String> linked = new LinkedHashSet<>(List.of("acc-2", "acc-3"));
        DebitCard card = new DebitCard("card-5", "4555", CardStatus.ACTIVE, CardType.DEBIT,
                "customer-5", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", linked);

        assertTrue(card.isAccountLinked("acc-1"));
        assertTrue(card.isAccountLinked("acc-2"));
        assertFalse(card.isAccountLinked("acc-x"));
    }

    @Test
    void canUseAccountMatchesLinkedCheck() {
        Set<String> linked = new LinkedHashSet<>(List.of("acc-2"));
        DebitCard card = new DebitCard("card-6", "4666", CardStatus.ACTIVE, CardType.DEBIT,
                "customer-6", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", linked);

        assertTrue(card.canUseAccount("acc-1"));
        assertTrue(card.canUseAccount("acc-2"));
        assertFalse(card.canUseAccount("acc-3"));
    }
}
