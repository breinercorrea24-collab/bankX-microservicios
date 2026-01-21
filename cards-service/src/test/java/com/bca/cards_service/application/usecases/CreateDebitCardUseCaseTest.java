package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.ports.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class CreateDebitCardUseCaseTest {

    private RecordingCardRepository cardRepository;
    private CreateDebitCardUseCase useCase;

    @BeforeEach
    void setUp() {
        cardRepository = new RecordingCardRepository();
        useCase = new CreateDebitCardUseCase(cardRepository);
    }

    @Test
    void executeSavesCard() {
        StepVerifier.create(useCase.execute("cust-1", CardType.DEBIT, BigDecimal.ZERO))
                .assertNext(card -> {
                    org.junit.jupiter.api.Assertions.assertNotNull(card);
                    org.junit.jupiter.api.Assertions.assertEquals(card, cardRepository.saved);
                })
                .verifyComplete();
    }

    @Test
    void propagateRepositoryError() {
        cardRepository.saveResult = Mono.error(new IllegalStateException("db error"));

        StepVerifier.create(useCase.execute("cust-err", CardType.DEBIT, BigDecimal.ONE))
                .expectError(IllegalStateException.class)
                .verify();
    }

    private static class RecordingCardRepository implements CardRepository {
        Card saved;
        Mono<Card> saveResult;
        Mono<Card> findByIdResult = Mono.empty();
        Mono<Boolean> findByCustomerResult = Mono.just(false);

        @Override
        public Mono<Card> save(Card card) {
            this.saved = card;
            return saveResult != null ? saveResult : Mono.just(card);
        }

        @Override
        public Mono<Card> findById(String cardId) {
            return findByIdResult;
        }

        @Override
        public Mono<Boolean> findByCustomerId(String customerId) {
            return findByCustomerResult;
        }
    }
}
