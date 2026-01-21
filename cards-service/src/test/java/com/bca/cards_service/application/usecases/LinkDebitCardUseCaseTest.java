package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkDebitCardUseCaseTest {

    private RecordingCardRepository cardRepository;
    private LinkDebitCardUseCase useCase;

    @BeforeEach
    void setUp() {
        cardRepository = new RecordingCardRepository();
        useCase = new LinkDebitCardUseCase(cardRepository);
    }

    @Test
    void linksAccountWhenNoneLinked() {
        DebitCard card = new DebitCard("card-1", "4111", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-1", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-primary", new LinkedHashSet<>());
        cardRepository.findByIdResult = Mono.just(card);

        StepVerifier.create(useCase.execute("card-1", "acc-2"))
                .assertNext(saved -> {
                    DebitCard savedCard = (DebitCard) saved;
                    assertEquals("acc-2", savedCard.getPrimaryAccountId());
                    assertTrue(savedCard.getLinkedAccountIds().contains("acc-2"));
                    cardRepository.saved = savedCard;
                })
                .verifyComplete();
    }

    @Test
    void addsLinkedAccountWhenExistingPresent() {
        LinkedHashSet<String> linked = new LinkedHashSet<>(Set.of("acc-1"));
        DebitCard card = new DebitCard("card-2", "4222", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-2", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", linked);
        cardRepository.findByIdResult = Mono.just(card);

        StepVerifier.create(useCase.execute("card-2", "acc-2"))
                .assertNext(saved -> {
                    DebitCard savedCard = (DebitCard) saved;
                    assertEquals("acc-1", savedCard.getPrimaryAccountId());
                    assertTrue(savedCard.getLinkedAccountIds().containsAll(Set.of("acc-1", "acc-2")));
                })
                .verifyComplete();
    }

    @Test
    void failsWhenCardIsNotDebit() {
        Card creditCard = new CreditCard("card-3", "4333", CardStatus.ACTIVE, CardType.CREDIT,
                "cust-3", "****", new BigDecimal("50.00"), LocalDateTime.now(), new BigDecimal("50.00"));
        cardRepository.findByIdResult = Mono.just(creditCard);

        StepVerifier.create(useCase.execute("card-3", "acc-x"))
                .expectError(BusinessException.class)
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
