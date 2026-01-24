package com.bca.cards_service.infrastructure.output.persistence;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.infrastructure.output.persistence.repository.CardMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardRepositoryAdapterTest {

    @Mock
    private CardMongoRepository cardMongoRepository;

    @InjectMocks
    private CardRepositoryAdapter adapter;

    @Test
    void saveReturnsSavedCard() {
        var card = sampleCard();
        when(cardMongoRepository.save(card)).thenReturn(Mono.just(card));

        StepVerifier.create(adapter.save(card))
                .expectNext(card)
                .verifyComplete();
    }

    @Test
    void savePropagatesError() {
        var card = sampleCard();
        when(cardMongoRepository.save(card)).thenReturn(Mono.error(new IllegalStateException("save error")));

        StepVerifier.create(adapter.save(card))
                .expectErrorMatches(err -> err instanceof IllegalStateException && err.getMessage().equals("save error"))
                .verify();
    }

    @Test
    void findByIdReturnsCard() {
        var card = sampleCard();
        when(cardMongoRepository.findById("card-1")).thenReturn(Mono.just(card));

        StepVerifier.create(adapter.findById("card-1"))
                .expectNext(card)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        when(cardMongoRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("missing"))
                .verifyComplete();
    }

    @Test
    void findByIdPropagatesError() {
        when(cardMongoRepository.findById("card-err")).thenReturn(Mono.error(new RuntimeException("find error")));

        StepVerifier.create(adapter.findById("card-err"))
                .expectErrorMessage("find error")
                .verify();
    }

    @Test
    void findByCustomerIdReturnsTrueWhenFound() {
        when(cardMongoRepository.findByCustomerId("cust-1")).thenReturn(Mono.just(sampleCard()));

        StepVerifier.create(adapter.findByCustomerId("cust-1"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByCustomerIdReturnsFalseWhenNotFound() {
        when(cardMongoRepository.findByCustomerId("cust-missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByCustomerId("cust-missing"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByCustomerIdPropagatesError() {
        when(cardMongoRepository.findByCustomerId("cust-err")).thenReturn(Mono.error(new IllegalArgumentException("customer error")));

        StepVerifier.create(adapter.findByCustomerId("cust-err"))
                .expectErrorMessage("customer error")
                .verify();
    }

    private DebitCard sampleCard() {
        return new DebitCard(
                "card-1",
                "1234",
                CardStatus.ACTIVE,
                CardType.DEBIT,
                "cust-1",
                "****1234",
                BigDecimal.TEN,
                LocalDateTime.now(),
                "acc-1",
                new HashSet<>());
    }
}
