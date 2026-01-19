package com.bca.cards_service.application.usecases.factory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;

@Component
public class CardFactory {
    public static Card create(CreateCardCommand cmd) {
        Card card;

        String id;
        String cardNumber = UUID.randomUUID().toString();
        String maskedNumber = cardNumber.substring(0, 4) + "****" + cardNumber.substring(24);

        switch (cmd.getCardType()) {
            case DEBIT:
                id = "card-deb-" + UUID.randomUUID().toString().substring(0, 8);
                Set<String> linkedAccountIds = Set.of();

                card = new DebitCard(
                    id, cardNumber, CardStatus.ACTIVE, cmd.getCardType(), cmd.getCustomerId(), maskedNumber,
                    BigDecimal.ZERO, LocalDateTime.now(),

                    "",
                    linkedAccountIds);
                break;
            case CREDIT:
                id = "card-cre-" + UUID.randomUUID().toString().substring(0, 8);

                card = new CreditCard(
                    id, cardNumber, CardStatus.ACTIVE, cmd.getCardType(), cmd.getCustomerId(), maskedNumber,
                    BigDecimal.ZERO, LocalDateTime.now(),

                    cmd.getCreditLimit());
                break;
            default:
                throw new BusinessException("Unsupported card type: " + cmd.getCardType());
        }

        return card;
    }
}
