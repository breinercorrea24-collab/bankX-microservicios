package com.bca.cards_service.application.usecases;


import com.bca.cards_service.application.usecases.factory.CardFactory;
import com.bca.cards_service.application.usecases.factory.CreateCardCommand;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCreditCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> execute(String customerId, CardType cardType, BigDecimal creditLimit) {
        log.info("Executing CreateCreditCardUseCase for customer: {} with credit limit: {}", customerId, creditLimit);
        String maskedNumber = generateMaskedNumber();
        String cardId = "card-cred-" + UUID.randomUUID().toString().substring(0, 8);
        /* Card card = Card.createCreditCard(cardId, customerId, maskedNumber, creditLimit); */

        Card card = CardFactory.create(new CreateCardCommand(customerId, "", cardType, creditLimit));

        log.debug("Generated card ID: {} with masked number: {}", cardId, maskedNumber);
        return cardRepository.save(card)
                .doOnSuccess(savedCard -> log.info("Successfully created credit card: {}", savedCard.getId()))
                .doOnError(error -> log.error("Failed to create credit card for customer: {}", customerId, error));
    }

    private String generateMaskedNumber() {
        // Simple implementation - in real world, this would integrate with card issuing system
        String number = String.format("%04d", (int)(Math.random() * 10000));
        return "**** **** **** " + number;
    }
}