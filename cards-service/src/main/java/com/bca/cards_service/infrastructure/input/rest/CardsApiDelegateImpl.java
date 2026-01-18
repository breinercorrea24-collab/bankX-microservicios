package com.bca.cards_service.infrastructure.input.rest;

import com.bca.cards_service.api.CardsApiDelegate;
import com.bca.cards_service.application.usecases.*;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.dto.CardBalanceResponse;
import com.bca.cards_service.dto.CardResponse;
import com.bca.cards_service.dto.CreateCreditCardRequest;
import com.bca.cards_service.dto.CreateDebitCardRequest;
import com.bca.cards_service.dto.DebitCardPaymentRequest;
import com.bca.cards_service.dto.DebitCardPaymentResponse;
import com.bca.cards_service.dto.DebitLinkRequest;
import com.bca.cards_service.infrastructure.input.mapper.CardsApiMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardsApiDelegateImpl implements CardsApiDelegate {

    private final CreateDebitCardUseCase createDebitCardUseCase;
    private final CreateCreditCardUseCase createCreditCardUseCase;
    private final LinkDebitCardUseCase linkDebitCardUseCase;
    private final GetCardBalanceUseCase getCardBalanceUseCase;
    private final PaymentCardsDebitUseCase paymentCardsDebitUseCase;

    @Override
    public Mono<ResponseEntity<DebitCardPaymentResponse>> cardsDebitCardIdPaymentsPost(String cardId,
            Mono<DebitCardPaymentRequest> debitCardPaymentRequest, ServerWebExchange exchange) {

        return debitCardPaymentRequest.flatMap(request -> {
            log.debug("Processing payment for card: {} with amount: {}", cardId, request.getAmount());
            return paymentCardsDebitUseCase.pay(cardId, BigDecimal.valueOf(request.getAmount()))
                    .map(paymentResult -> {
                        DebitCardPaymentResponse response = new DebitCardPaymentResponse();
                        // response.setTransactionId(paymentResult.getTransactionId());
                        // response.setStatus(paymentResult.getStatus());
                        log.info("Payment processed successfully for debit card: {}", cardId);
                        return ResponseEntity.ok(response);
                    });
        }).doOnError(error -> log.error("Failed to process payment for debit card: {}", cardId, error));

    }

    @Override
    public Mono<ResponseEntity<Boolean>> cardsCustomerCustomerIdExistsGet(String customerId,
            ServerWebExchange exchange) {
        log.info("Received request to check if customer has a card");
        return getCardBalanceUseCase.hasCards(customerId);
    }

    @Override
    public Mono<ResponseEntity<CardResponse>> cardsDebitPost(Mono<CreateDebitCardRequest> createDebitCardRequest,
            ServerWebExchange exchange) {
        log.info("Received request to create debit card");
        return createDebitCardRequest.flatMap((CreateDebitCardRequest request) -> {
            log.debug("Processing debit card creation for customer: {}", request.getCustomerId());

            return createDebitCardUseCase
                    .execute(request.getCustomerId(), CardType.DEBIT, BigDecimal.ZERO)
                    .map(CardsApiMapper::mapToCardResponse)
                    .map(cardResponse -> {
                        log.info("Debit card created successfully with ID: {}", cardResponse.getId());
                        return ResponseEntity.status(201).body(cardResponse);
                    });
        }).doOnError(error -> log.error("Failed to create debit card", error));
    }

    @Override
    public Mono<ResponseEntity<CardResponse>> cardsCreditPost(Mono<CreateCreditCardRequest> createCreditCardRequest,
            ServerWebExchange exchange) {
        log.info("Received request to create credit card");
        return createCreditCardRequest.flatMap((CreateCreditCardRequest request) -> {
            log.debug("Processing credit card creation for customer: {} with limit: {}", request.getCustomerId(),
                    request.getCreditLimit());

            BigDecimal creditLimit = BigDecimal.valueOf(request.getCreditLimit());
            return createCreditCardUseCase
                    .execute(request.getCustomerId(), CardType.CREDIT, creditLimit)
                    .map(CardsApiMapper::mapToCardResponse)
                    .map(cardResponse -> {
                        log.info("Credit card created successfully with ID: {}", cardResponse.getId());
                        return ResponseEntity.status(201).body(cardResponse);
                    });
        }).doOnError(error -> log.error("Failed to create credit card", error));
    }

    @Override
    public Mono<ResponseEntity<Void>> cardsDebitLinkPost(Mono<DebitLinkRequest> debitLinkRequest,
            ServerWebExchange exchange) {
        log.info("Received request to link debit card to account");
        return debitLinkRequest.flatMap((DebitLinkRequest request) -> {
            log.debug("Processing link request for card: {} to account: {}", request.getCardId(),
                    request.getAccountId());
            return linkDebitCardUseCase.execute(request.getCardId(), request.getAccountId())
                    .then(Mono.just(ResponseEntity.ok().<Void>build()));
        }).doOnSuccess(response -> log.info("Debit card link request processed successfully"))
                .doOnError(error -> log.error("Failed to link debit card to account", error));
    }

    @Override
    public Mono<ResponseEntity<CardBalanceResponse>> cardsCardIdBalanceGet(String cardId, ServerWebExchange exchange) {
        log.info("Received request to get balance for card: {}", cardId);
        return getCardBalanceUseCase.execute(cardId)
                .map(balance -> {
                    CardBalanceResponse response = new CardBalanceResponse();
                    response.setCardId(balance.getId());
                    /*
                     * response.setType(CardBalanceResponse.TypeEnum.valueOf(balance.getType().name(
                     * )));
                     * response.setCurrency(balance.getCurrency());
                     * response.setBalance(balance.getBalance().floatValue());
                     * response.setAvailableBalance(balance.getAvailableBalance().floatValue());
                     */
                    log.info("Balance retrieved successfully for card: {}", cardId);
                    return ResponseEntity.ok(response);
                }).doOnError(error -> log.error("Failed to get balance for card: {}", cardId, error));
    }

    
}