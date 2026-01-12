package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.CreditsApiDelegate;
import com.bca.core_banking_service.domain.ports.input.CreditUseCase;
import com.bca.core_banking_service.dto.*;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CreditApiDelegateImpl implements CreditsApiDelegate {

    private final CreditUseCase creditUseCase;

    @Override
    public Mono<ResponseEntity<CreditResponse>> creditsPost(Mono<CreditCreate> creditCreate,
            ServerWebExchange exchange) {
        return creditCreate
                .flatMap(request -> creditUseCase.createCredit(
                        request.getCustomerId(),
                        Credit.CreditType.valueOf(request.getCreditType().getValue()),
                        BigDecimal.valueOf(request.getAmount()),
                        request.getTermMonths(),
                        BigDecimal.valueOf(request.getInterestRate())))
                .map(this::mapToCreditResponse)
                .map(creditResponse -> ResponseEntity.status(HttpStatus.CREATED).body(creditResponse))
                .onErrorResume(RuntimeException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @Override
    public Mono<ResponseEntity<CreditPaymentResponse>> creditsCreditIdPaymentPost(String creditId,
            Mono<AmountRequest> amountRequest,
            ServerWebExchange exchange) {
        return amountRequest
                .flatMap(request -> {
                    BigDecimal amount = BigDecimal.valueOf(request.getAmount());
                    return creditUseCase.payCredit(creditId, amount)
                            .map(credit -> mapToCreditPaymentResponse(credit, amount))
                            .map(creditPaymentResponse -> ResponseEntity.ok(creditPaymentResponse));
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Payment amount exceeds pending debt")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    private CreditResponse mapToCreditResponse(Credit credit) {
        CreditResponse response = new CreditResponse();
        response.setId(credit.getId());
        response.setCustomerId(credit.getCustomerId());
        response.setCreditType(CreditResponse.CreditTypeEnum.valueOf(credit.getCreditType().name()));
        response.setOriginalAmount(credit.getOriginalAmount().doubleValue());
        response.setPendingDebt(credit.getPendingDebt().doubleValue());
        response.setInterestRate(credit.getInterestRate().doubleValue());
        response.setTermMonths(credit.getTermMonths());
        response.setStatus(credit.getStatus().name());
        response.setCreatedAt(OffsetDateTime.ofInstant(credit.getCreatedAt().toInstant(java.time.ZoneOffset.UTC),
                java.time.ZoneOffset.UTC));
        return response;
    }

    private CreditPaymentResponse mapToCreditPaymentResponse(Credit credit, BigDecimal paidAmount) {
        CreditPaymentResponse response = new CreditPaymentResponse();
        response.setCreditId(credit.getId());
        response.setPaymentId("pay-" + System.currentTimeMillis());
        response.setPaidAmount(paidAmount.doubleValue());
        response.setRemainingDebt(credit.getPendingDebt().doubleValue());
        response.setStatus(
                credit.getPendingDebt().compareTo(BigDecimal.ZERO) == 0 ? CreditPaymentResponse.StatusEnum.FULL_PAYMENT
                        : CreditPaymentResponse.StatusEnum.PARTIAL_PAYMENT);
        response.setPaidAt(OffsetDateTime.now());
        return response;
    }
}
