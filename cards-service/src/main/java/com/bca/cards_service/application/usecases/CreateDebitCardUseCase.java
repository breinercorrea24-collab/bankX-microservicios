package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.CustomerId;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDebitCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> execute(CustomerId customerId) {
        log.info("Executing CreateDebitCardUseCase for customer: {}", customerId.value());
        String maskedNumber = generateMaskedNumber();
        CardId cardId = new CardId("card-deb-" + UUID.randomUUID().toString().substring(0, 8));
        Card card = Card.createDebitCard(cardId, customerId, maskedNumber);
        log.debug("Generated card ID: {} with masked number: {}", cardId.value(), maskedNumber);
        return cardRepository.save(card)
                .doOnSuccess(savedCard -> log.info("Successfully created debit card: {}", savedCard.id().value()))
                .doOnError(error -> log.error("Failed to create debit card for customer: {}", customerId.value(), error));
    }

    private String generateMaskedNumber() {
        // Simple implementation - in real world, this would integrate with card issuing system
        String number = String.format("%04d", (int)(Math.random() * 10000));
        return "**** **** **** " + number;
    }
}