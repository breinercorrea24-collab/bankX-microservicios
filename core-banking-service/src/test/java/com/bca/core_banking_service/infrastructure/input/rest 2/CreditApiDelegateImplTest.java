package com.bca.core_banking_service.infrastructure.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.application.ports.input.usecases.CreditUseCase;
import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.CreditCreate;
import com.bca.core_banking_service.dto.CreditPaymentResponse;
import com.bca.core_banking_service.dto.CreditResponse;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreditApiDelegateImplTest {

    @Mock
    private CreditUseCase creditUseCase;

    private CreditApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        delegate = new CreditApiDelegateImpl(creditUseCase);
    }

    @Test
    void creditsPost_returnsCreatedCreditResponse() {
        Credit credit = sampleCredit();
        CreditCreate request = new CreditCreate()
                .customerId("customer-1")
                .creditType(CreditCreate.CreditTypeEnum.PERSONAL_LOAN)
                .amount(1000F)
                .termMonths(12)
                .interestRate(5F);

        org.mockito.Mockito.lenient().when(creditUseCase.createCredit(
                "customer-1",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000.0),
                12,
                BigDecimal.valueOf(5.0)))
                .thenReturn(Mono.just(credit));

        StepVerifier.create(delegate.creditsPost(Mono.just(request), mockExchange("/credits")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    CreditResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals("credit-1", body.getId());
                    assertEquals("customer-1", body.getCustomerId());
                })
                .verifyComplete();
    }

    @Test
    void creditsCreditIdPaymentPost_returnsPaymentResponse() {
        Credit credit = sampleCredit();
        credit.setPendingDebt(BigDecimal.valueOf(200));
        AmountRequest request = new AmountRequest().amount(150F);

        org.mockito.Mockito.lenient().when(creditUseCase.payCredit("credit-1", BigDecimal.valueOf(150.0)))
                .thenReturn(Mono.just(credit));

        StepVerifier.create(delegate.creditsCreditIdPaymentPost(
                "credit-1", Mono.just(request), mockExchange("/credits/credit-1/payment")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    CreditPaymentResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals("credit-1", body.getCreditId());
                    assertEquals(150.0, body.getPaidAmount());
                })
                .verifyComplete();
    }

    private Credit sampleCredit() {
        return new Credit(
                "credit-1",
                "customer-1",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5),
                12,
                Credit.CreditStatus.ACTIVE,
                LocalDateTime.now());
    }

    private MockServerWebExchange mockExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.post(path).build());
    }
}
