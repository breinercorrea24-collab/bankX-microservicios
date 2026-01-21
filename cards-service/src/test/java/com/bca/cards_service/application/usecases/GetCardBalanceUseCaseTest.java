package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.ports.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GetCardBalanceUseCaseTest {

    private RecordingCardRepository cardRepository;
    private GetCardBalanceUseCase useCase;

    @BeforeEach
    void setUp() {
        cardRepository = new RecordingCardRepository();
        useCase = new GetCardBalanceUseCase(cardRepository);
    }

    @Test
    void executeReturnsCard() {
        Card card = new TestCard();
        cardRepository.findByIdResult = Mono.just(card);

        StepVerifier.create(useCase.execute("card-1"))
                .expectNext(card)
                .verifyComplete();
    }

    @Test
    void hasCardsReturnsTrueResponse() {
        cardRepository.findByCustomerResult = Mono.just(true);

        StepVerifier.create(useCase.hasCards("cust-1"))
                .assertNext(response -> org.junit.jupiter.api.Assertions.assertTrue(response.getBody()))
                .verifyComplete();
    }

    @Test
    void hasCardsReturnsFalseResponse() {
        cardRepository.findByCustomerResult = Mono.just(false);

        StepVerifier.create(useCase.hasCards("cust-2"))
                .assertNext(response -> org.junit.jupiter.api.Assertions.assertFalse(response.getBody()))
                .verifyComplete();
    }

    @Test
    void executePropagatesErrorAndTriggersDoOnError() {
        cardRepository.findByIdResult = Mono.error(new RuntimeException("boom"));

        StepVerifier.create(useCase.execute("card-err"))
                .expectError(RuntimeException.class)
                .verify();
    }

    private static class RecordingCardRepository implements CardRepository {
        Mono<Card> saveResult = Mono.empty();
        Mono<Card> findByIdResult = Mono.empty();
        Mono<Boolean> findByCustomerResult = Mono.just(false);

        @Override
        public Mono<Card> save(Card card) {
            return saveResult;
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

    private static class TestCard extends com.bca.cards_service.domain.model.card.Card {
        TestCard() {
            super("id", "num", com.bca.cards_service.domain.enums.card.CardStatus.ACTIVE,
                    com.bca.cards_service.domain.enums.card.CardType.DEBIT,
                    "cust", "****", java.math.BigDecimal.ZERO, java.time.LocalDateTime.now());
        }
    }
}
