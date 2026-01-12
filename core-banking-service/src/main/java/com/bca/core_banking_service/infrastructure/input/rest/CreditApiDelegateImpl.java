package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.CreditsApiDelegate;
import com.bca.core_banking_service.application.ports.input.usecases.CreditUseCase;
import com.bca.core_banking_service.dto.*;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.input.mapper.CreditApiMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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
                .map(CreditApiMapper::mapToCreditResponse)
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
                            .map(credit -> CreditApiMapper.mapToCreditPaymentResponse(credit, amount))
                            .map(creditPaymentResponse -> ResponseEntity.ok(creditPaymentResponse));
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("Payment amount exceeds pending debt")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

}
