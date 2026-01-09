package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.CustomerId;
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

    public Mono<Card> execute(CustomerId customerId, BigDecimal creditLimit) {
        log.info("Executing CreateCreditCardUseCase for customer: {} with credit limit: {}", customerId.value(), creditLimit);
        String maskedNumber = generateMaskedNumber();
        CardId cardId = new CardId("card-cred-" + UUID.randomUUID().toString().substring(0, 8));
        Card card = Card.createCreditCard(cardId, customerId, maskedNumber, creditLimit);
        log.debug("Generated card ID: {} with masked number: {}", cardId.value(), maskedNumber);
        return cardRepository.save(card)
                .doOnSuccess(savedCard -> log.info("Successfully created credit card: {}", savedCard.id().value()))
                .doOnError(error -> log.error("Failed to create credit card for customer: {}", customerId.value(), error));
    }

    private String generateMaskedNumber() {
        // Simple implementation - in real world, this would integrate with card issuing system
        String number = String.format("%04d", (int)(Math.random() * 10000));
        return "**** **** **** " + number;
    }
}