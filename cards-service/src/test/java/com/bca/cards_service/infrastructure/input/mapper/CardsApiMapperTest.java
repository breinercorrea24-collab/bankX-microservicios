package com.bca.cards_service.infrastructure.input.mapper;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.dto.CardResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardsApiMapperTest {

    @Test
    void mapsAllFieldsIncludingCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 30);
        DebitCard card = new DebitCard(
                "card-1",
                "1111-2222",
                CardStatus.ACTIVE,
                CardType.CREDIT,
                "cust-1",
                "****2222",
                new BigDecimal("123.45"),
                createdAt,
                "acc-1",
                java.util.Set.of());

        CardResponse response = CardsApiMapper.mapToCardResponse(card);

        assertEquals("card-1", response.getId());
        assertEquals(CardResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(CardResponse.TypeEnum.CREDIT, response.getType());
        assertEquals("cust-1", response.getCustomerId());
        assertEquals("****2222", response.getMaskedNumber());
        assertEquals(createdAt.atZone(ZoneOffset.UTC).toOffsetDateTime(), response.getCreatedAt());
        assertEquals(123.45f, response.getAvailableLimit());
    }

    @Test
    void mapsWhenOptionalFieldsAreNull() {
        DebitCard card = new DebitCard(
                "card-2",
                "3333-4444",
                CardStatus.ACTIVE,
                CardType.DEBIT,
                null,
                "****4444",
                BigDecimal.ZERO,
                null,
                "acc-2",
                java.util.Set.of());

        CardResponse response = CardsApiMapper.mapToCardResponse(card);

        assertEquals("card-2", response.getId());
        assertEquals(CardResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(CardResponse.TypeEnum.DEBIT, response.getType());
        assertNull(response.getCustomerId());
        assertNull(response.getCreatedAt());
    }

    @Test
    void constructorIsNotAccessible() throws Exception {
        Constructor<CardsApiMapper> ctor = CardsApiMapper.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        var ex = assertThrows(java.lang.reflect.InvocationTargetException.class, ctor::newInstance);
        assertEquals(IllegalStateException.class, ex.getCause().getClass());
    }

}
