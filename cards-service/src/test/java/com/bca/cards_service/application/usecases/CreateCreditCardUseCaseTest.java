package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.ports.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class CreateCreditCardUseCaseTest {

    private RecordingCardRepository cardRepository;
    private CreateCreditCardUseCase useCase;

    @BeforeEach
    void setUp() {
        cardRepository = new RecordingCardRepository();
        useCase = new CreateCreditCardUseCase(cardRepository);
    }

    @Test
    void executeSavesCard() {
        StepVerifier.create(useCase.execute("cust-1", CardType.CREDIT, new BigDecimal("5000")))
                .assertNext(card -> {
                    org.junit.jupiter.api.Assertions.assertInstanceOf(CreditCard.class, card);
                    org.junit.jupiter.api.Assertions.assertEquals(card, cardRepository.saved);
                })
                .verifyComplete();
    }

    @Test
    void propagateRepositoryError() {
        cardRepository.saveResult = Mono.error(new IllegalStateException("db error"));

        StepVerifier.create(useCase.execute("cust-err", CardType.CREDIT, BigDecimal.ONE))
                .expectError(IllegalStateException.class)
                .verify();
    }

    private static class RecordingCardRepository implements CardRepository {
        com.bca.cards_service.domain.model.card.Card saved;
        Mono<com.bca.cards_service.domain.model.card.Card> saveResult;
        Mono<com.bca.cards_service.domain.model.card.Card> findByIdResult = Mono.empty();
        Mono<Boolean> findByCustomerResult = Mono.just(false);

        @Override
        public Mono<com.bca.cards_service.domain.model.card.Card> save(com.bca.cards_service.domain.model.card.Card card) {
            this.saved = card;
            return saveResult != null ? saveResult : Mono.just(card);
        }

        @Override
        public Mono<com.bca.cards_service.domain.model.card.Card> findById(String cardId) {
            return findByIdResult;
        }

        @Override
        public Mono<Boolean> findByCustomerId(String customerId) {
            return findByCustomerResult;
        }
    }
}
