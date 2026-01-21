package com.bca.cards_service.application.usecases.factory;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CardFactoryTest {

    @Test
    void createsDebitCardWithMaskedNumber() {
        CreateCardCommand cmd = new CreateCardCommand(
                "customer-1",
                "account-1",
                CardType.DEBIT,
                BigDecimal.TEN);

        Card result = CardFactory.create(cmd);

        assertTrue(result instanceof DebitCard);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        assertEquals(CardType.DEBIT, result.getType());
        assertTrue(result.getId().startsWith("card-deb-"));

        // Masked number keeps the first 4 and last part, with a literal "****" in between
        String cardNumber = result.getCardNumber();
        String masked = result.getMaskedNumber();
        assertEquals(cardNumber.substring(0, 4) + "****" + cardNumber.substring(24), masked);
    }

    @Test
    void createsCreditCardWithLimit() {
        BigDecimal limit = new BigDecimal("5000");
        CreateCardCommand cmd = new CreateCardCommand(
                "customer-2",
                "account-2",
                CardType.CREDIT,
                limit);

        Card result = CardFactory.create(cmd);

        assertTrue(result instanceof CreditCard);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        assertEquals(CardType.CREDIT, result.getType());
        assertTrue(result.getId().startsWith("card-cre-"));

        CreditCard credit = (CreditCard) result;
        assertEquals(limit, credit.getAvailableCredit());
    }

    @Test
    void throwsWhenCardTypeNotMappedInSwitch() throws Exception {
        // Force the synthetic switch map to redirect DEBIT to the default branch
        Class<?> switchClass = Class.forName("com.bca.cards_service.application.usecases.factory.CardFactory$1");
        Field field = switchClass.getDeclaredField("$SwitchMap$com$bca$cards_service$domain$enums$card$CardType");
        field.setAccessible(true);
        int[] mapping = (int[]) field.get(null);
        int[] original = mapping.clone();
        Arrays.fill(mapping, 0);
        try {
            CreateCardCommand cmd = new CreateCardCommand(
                    "customer-3",
                    "account-3",
                    CardType.DEBIT,
                    BigDecimal.ONE);

            assertThrows(BusinessException.class, () -> CardFactory.create(cmd));
        } finally {
            System.arraycopy(original, 0, mapping, 0, original.length);
        }
    }
}
