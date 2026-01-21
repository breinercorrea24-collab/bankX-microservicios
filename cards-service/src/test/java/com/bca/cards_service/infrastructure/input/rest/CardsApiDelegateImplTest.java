package com.bca.cards_service.infrastructure.input.rest;

import com.bca.cards_service.application.usecases.CreateCreditCardUseCase;
import com.bca.cards_service.application.usecases.CreateDebitCardUseCase;
import com.bca.cards_service.application.usecases.GetCardBalanceUseCase;
import com.bca.cards_service.application.usecases.LinkDebitCardUseCase;
import com.bca.cards_service.application.usecases.PaymentCardsDebitUseCase;
import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.dto.CardResponse;
import com.bca.cards_service.dto.CreateCreditCardRequest;
import com.bca.cards_service.dto.CreateDebitCardRequest;
import com.bca.cards_service.dto.DebitCardPaymentRequest;
import com.bca.cards_service.dto.DebitCardPaymentResponse;
import com.bca.cards_service.dto.DebitLinkRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardsApiDelegateImplTest {

    @Mock
    private CreateDebitCardUseCase createDebitCardUseCase;
    @Mock
    private CreateCreditCardUseCase createCreditCardUseCase;
    @Mock
    private LinkDebitCardUseCase linkDebitCardUseCase;
    @Mock
    private GetCardBalanceUseCase getCardBalanceUseCase;
    @Mock
    private PaymentCardsDebitUseCase paymentCardsDebitUseCase;

    @InjectMocks
    private CardsApiDelegateImpl delegate;

    @Test
    void cardsDebitCardIdPaymentsPostReturnsResponseOnSuccess() {
        DebitCardPaymentRequest request = new DebitCardPaymentRequest("acc-1", 20.5f);
        when(paymentCardsDebitUseCase.pay("card-1", BigDecimal.valueOf(request.getAmount())))
                .thenReturn(Mono.just(voidValue()));

        Mono<ResponseEntity<DebitCardPaymentResponse>> result = delegate.cardsDebitCardIdPaymentsPost(
                "card-1",
                Mono.just(request),
                exchange());

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("acc-1", response.getBody().getAccountId());
                    assertEquals(20.5f, response.getBody().getAmount());
                })
                .verifyComplete();
    }

    @Test
    void cardsDebitCardIdPaymentsPostPropagatesErrors() {
        DebitCardPaymentRequest request = new DebitCardPaymentRequest("acc-err", 40f);
        when(paymentCardsDebitUseCase.pay(eq("card-err"), any(BigDecimal.class)))
                .thenReturn(Mono.error(new IllegalStateException("fail")));

        Mono<ResponseEntity<DebitCardPaymentResponse>> result = delegate.cardsDebitCardIdPaymentsPost(
                "card-err",
                Mono.just(request),
                exchange());

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void cardsCustomerCustomerIdExistsGetReturnsHasCards() {
        when(getCardBalanceUseCase.hasCards("customer-1")).thenReturn(Mono.just(ResponseEntity.ok(true)));

        Mono<ResponseEntity<Boolean>> result = delegate.cardsCustomerCustomerIdExistsGet("customer-1", exchange());

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(Boolean.TRUE, response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void cardsDebitPostCreatesCardSuccessfully() {
        CreateDebitCardRequest request = new CreateDebitCardRequest("cust-deb", CreateDebitCardRequest.CardTypeEnum.DEBIT);
        DebitCard card = new DebitCard(
                "card-deb-1",
                "1111",
                CardStatus.ACTIVE,
                CardType.DEBIT,
                "cust-deb",
                "****1111",
                BigDecimal.TEN,
                LocalDateTime.now(),
                "acc-1",
                new HashSet<>());
        when(createDebitCardUseCase.execute("cust-deb", CardType.DEBIT, BigDecimal.ZERO)).thenReturn(Mono.just(card));

        Mono<ResponseEntity<CardResponse>> result = delegate.cardsDebitPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("card-deb-1", response.getBody().getId());
                    assertEquals(CardResponse.TypeEnum.DEBIT, response.getBody().getType());
                })
                .verifyComplete();
    }

    @Test
    void cardsDebitPostPropagatesErrors() {
        CreateDebitCardRequest request = new CreateDebitCardRequest("cust-deb", CreateDebitCardRequest.CardTypeEnum.DEBIT);
        when(createDebitCardUseCase.execute("cust-deb", CardType.DEBIT, BigDecimal.ZERO))
                .thenReturn(Mono.error(new IllegalArgumentException("fail debit")));

        Mono<ResponseEntity<CardResponse>> result = delegate.cardsDebitPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void cardsCreditPostCreatesCardSuccessfully() {
        CreateCreditCardRequest request = new CreateCreditCardRequest(
                "cust-cred",
                CreateCreditCardRequest.CardTypeEnum.CREDIT,
                5000f);
        CreditCard card = new CreditCard(
                "card-cred-1",
                "2222",
                CardStatus.ACTIVE,
                CardType.CREDIT,
                "cust-cred",
                "****2222",
                BigDecimal.valueOf(5000),
                LocalDateTime.now(),
                BigDecimal.valueOf(5000));
        when(createCreditCardUseCase.execute("cust-cred", CardType.CREDIT, BigDecimal.valueOf(request.getCreditLimit())))
                .thenReturn(Mono.just(card));

        Mono<ResponseEntity<CardResponse>> result = delegate.cardsCreditPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("card-cred-1", response.getBody().getId());
                    assertEquals(CardResponse.TypeEnum.CREDIT, response.getBody().getType());
                })
                .verifyComplete();
    }

    @Test
    void cardsCreditPostPropagatesErrors() {
        CreateCreditCardRequest request = new CreateCreditCardRequest(
                "cust-cred",
                CreateCreditCardRequest.CardTypeEnum.CREDIT,
                5000f);
        when(createCreditCardUseCase.execute("cust-cred", CardType.CREDIT, BigDecimal.valueOf(request.getCreditLimit())))
                .thenReturn(Mono.error(new IllegalStateException("fail credit")));

        Mono<ResponseEntity<CardResponse>> result = delegate.cardsCreditPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void cardsDebitLinkPostLinksCard() {
        DebitLinkRequest request = new DebitLinkRequest("card-1", "acc-2");
        when(linkDebitCardUseCase.execute("card-1", "acc-2"))
                .thenReturn(Mono.just(buildDebitCard("card-1")));

        Mono<ResponseEntity<Void>> result = delegate.cardsDebitLinkPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(HttpStatus.OK, response.getStatusCode()))
                .verifyComplete();
    }

    @Test
    void cardsDebitLinkPostPropagatesErrors() {
        DebitLinkRequest request = new DebitLinkRequest("card-err", "acc-err");
        when(linkDebitCardUseCase.execute("card-err", "acc-err"))
                .thenReturn(Mono.error(new RuntimeException("fail link")));

        Mono<ResponseEntity<Void>> result = delegate.cardsDebitLinkPost(Mono.just(request), exchange());

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void cardsCardIdBalanceGetReturnsResponse() {
        when(getCardBalanceUseCase.execute("card-bal"))
                .thenReturn(Mono.just(buildDebitCard("card-bal")));

        Mono<ResponseEntity<com.bca.cards_service.dto.CardBalanceResponse>> result = delegate.cardsCardIdBalanceGet(
                "card-bal",
                exchange());

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("card-bal", response.getBody().getCardId());
                })
                .verifyComplete();
    }

    @Test
    void cardsCardIdBalanceGetPropagatesErrors() {
        when(getCardBalanceUseCase.execute("card-bal"))
                .thenReturn(Mono.error(new RuntimeException("fail balance")));

        Mono<ResponseEntity<com.bca.cards_service.dto.CardBalanceResponse>> result = delegate.cardsCardIdBalanceGet(
                "card-bal",
                exchange());

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    private DebitCard buildDebitCard(String id) {
        return new DebitCard(
                id,
                "9999",
                CardStatus.ACTIVE,
                CardType.DEBIT,
                "cust",
                "****9999",
                BigDecimal.ONE,
                LocalDateTime.now(),
                "acc-main",
                new HashSet<>());
    }

    private Void voidValue() {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Void) ((Unsafe) field.get(null)).allocateInstance(Void.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
    }
}
