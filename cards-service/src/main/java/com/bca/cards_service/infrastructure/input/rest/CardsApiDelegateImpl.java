package com.bca.cards_service.infrastructure.input.rest;

import com.bca.cards_service.api.CardsApiDelegate;
import com.bca.cards_service.application.usecases.*;
import com.bca.cards_service.domain.model.AccountId;
import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.CustomerId;
import com.bca.cards_service.dto.CardBalanceResponse;
import com.bca.cards_service.dto.CardResponse;
import com.bca.cards_service.dto.CreateCreditCardRequest;
import com.bca.cards_service.dto.CreateDebitCardRequest;
import com.bca.cards_service.dto.DebitLinkRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardsApiDelegateImpl implements CardsApiDelegate {

    private final CreateDebitCardUseCase createDebitCardUseCase;
    private final CreateCreditCardUseCase createCreditCardUseCase;
    private final LinkDebitCardUseCase linkDebitCardUseCase;
    private final GetCardBalanceUseCase getCardBalanceUseCase;

    @Override
    public Mono<ResponseEntity<Boolean>> cardsCustomerCustomerIdExistsGet(String customerId,
        ServerWebExchange exchange){
        log.info("Received request to check if customer has a card");
        return getCardBalanceUseCase.execute(customerId)
                .map(balance -> ResponseEntity.ok(true))
                .onErrorReturn(ResponseEntity.notFound().build());
    }   

    @Override
    public Mono<ResponseEntity<CardResponse>> cardsDebitPost(Mono<CreateDebitCardRequest> createDebitCardRequest, ServerWebExchange exchange) {
        log.info("Received request to create debit card");
        return createDebitCardRequest.flatMap(request -> {
            log.debug("Processing debit card creation for customer: {}", request.getCustomerId());
            CustomerId customerId = new CustomerId(request.getCustomerId());
            return createDebitCardUseCase.execute(customerId)
                    .map(this::mapToCardResponse)
                    .map(cardResponse -> {
                        log.info("Debit card created successfully with ID: {}", cardResponse.getId());
                        return ResponseEntity.status(201).body(cardResponse);
                    });
        }).doOnError(error -> log.error("Failed to create debit card", error));
    }

    @Override
    public Mono<ResponseEntity<CardResponse>> cardsCreditPost(Mono<CreateCreditCardRequest> createCreditCardRequest, ServerWebExchange exchange) {
        log.info("Received request to create credit card");
        return createCreditCardRequest.flatMap(request -> {
            log.debug("Processing credit card creation for customer: {} with limit: {}", request.getCustomerId(), request.getCreditLimit());
            CustomerId customerId = new CustomerId(request.getCustomerId());
            BigDecimal creditLimit = BigDecimal.valueOf(request.getCreditLimit());
            return createCreditCardUseCase.execute(customerId, creditLimit)
                    .map(this::mapToCardResponse)
                    .map(cardResponse -> {
                        log.info("Credit card created successfully with ID: {}", cardResponse.getId());
                        return ResponseEntity.status(201).body(cardResponse);
                    });
        }).doOnError(error -> log.error("Failed to create credit card", error));
    }

    @Override
    public Mono<ResponseEntity<Void>> cardsDebitLinkPost(Mono<DebitLinkRequest> debitLinkRequest, ServerWebExchange exchange) {
        log.info("Received request to link debit card to account");
        return debitLinkRequest.flatMap(request -> {
            log.debug("Processing link request for card: {} to account: {}", request.getCardId(), request.getAccountId());
            CardId cardId = new CardId(request.getCardId());
            AccountId accountId = new AccountId(request.getAccountId());
            return linkDebitCardUseCase.execute(cardId, accountId)
                    .then(Mono.just(ResponseEntity.ok().<Void>build()));
        }).doOnSuccess(response -> log.info("Debit card link request processed successfully"))
          .doOnError(error -> log.error("Failed to link debit card to account", error));
    }

    @Override
    public Mono<ResponseEntity<CardBalanceResponse>> cardsCardIdBalanceGet(String cardId, ServerWebExchange exchange) {
        log.info("Received request to get balance for card: {}", cardId);
        CardId id = new CardId(cardId);
        return getCardBalanceUseCase.execute(id)
                .map(balance -> {
                    CardBalanceResponse response = new CardBalanceResponse();
                    response.setCardId(balance.cardId().value());
                    response.setType(CardBalanceResponse.TypeEnum.valueOf(balance.type().name()));
                    response.setCurrency(balance.currency());
                    response.setBalance(balance.balance().floatValue());
                    response.setAvailableBalance(balance.availableBalance().floatValue());
                    log.info("Balance retrieved successfully for card: {}", cardId);
                    log.debug("Balance details - type: {}, balance: {}, available: {}", balance.type(), balance.balance(), balance.availableBalance());
                    return ResponseEntity.ok(response);
                }).doOnError(error -> log.error("Failed to get balance for card: {}", cardId, error));
    }

    private CardResponse mapToCardResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.id().value());
        response.setType(CardResponse.TypeEnum.valueOf(card.type().name()));
        response.setCustomerId(card.customerId().value());
        response.setMaskedNumber(card.maskedNumber());
        response.setStatus(CardResponse.StatusEnum.valueOf(card.status().name()));
        response.setCreatedAt(card.createdAt().atZone(ZoneOffset.UTC).toOffsetDateTime());
        if (card.isCredit()) {
            response.setCreditLimit(card.creditLimit().floatValue());
            response.setAvailableLimit(card.availableLimit().floatValue());
        }
        return response;
    }
}