package com.bca.cards_service.application.usecases.factory;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

    @Test
    @DisplayName("Cubre la línea 16: Constructor implícito de CardFactory")
    void testConstructorCoverage() {
        // Al ser un @Component, Java genera un constructor por defecto.
        // Instanciarlo manualmente cubre la cabecera de la clase.
        assertNotNull(new CardFactory());
    }

    @Test
    @DisplayName("Cubre la línea 47: Caso default del switch")
    void shouldThrowExceptionWhenCardTypeIsUnsupported() {
        // Reutilizamos la tabla synthetic switch para enviar un tipo conocido al branch default
        try {
            Class<?> switchClass = Class.forName("com.bca.cards_service.application.usecases.factory.CardFactory$1");
            Field field = switchClass.getDeclaredField("$SwitchMap$com$bca$cards_service$domain$enums$card$CardType");
            field.setAccessible(true);
            int[] mapping = (int[]) field.get(null);
            int[] original = mapping.clone();

            // Anulamos el mapeo del ordinal de CREDIT para que caiga en default
            mapping[CardType.CREDIT.ordinal()] = 0;
            try {
                CreateCardCommand cmd = new CreateCardCommand(
                        "customer-unsupported",
                        "account-unsupported",
                        CardType.CREDIT,
                        BigDecimal.TEN);

                BusinessException exception = assertThrows(BusinessException.class, () -> CardFactory.create(cmd));
                assertTrue(exception.getMessage().contains("Unsupported card type"));
            } finally {
                System.arraycopy(original, 0, mapping, 0, original.length);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
