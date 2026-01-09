package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.ports.AccountService;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCardBalanceUseCase {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public Mono<CardBalance> execute(CardId cardId) {
        log.info("Executing GetCardBalanceUseCase for card: {}", cardId.value());
        return cardRepository.findById(cardId)
                .flatMap(card -> {
                    log.debug("Found card: {} of type: {}", cardId.value(), card.type());
                    if (card.isDebit()) {
                        if (card.linkedAccountId() == null) {
                            log.warn("Debit card {} is not linked to any account", cardId.value());
                            return Mono.error(new IllegalStateException("Debit card is not linked to any account"));
                        }
                        log.debug("Fetching balance for account: {}", card.linkedAccountId().value());
                        return accountService.getAccountBalance(card.linkedAccountId())
                                .map(balance -> {
                                    log.debug("Account balance: {}", balance);
                                    return new CardBalance(card.id(), card.type(), "PEN", balance, balance);
                                });
                    } else {
                        // For credit cards, balance is the used limit
                        BigDecimal usedLimit = card.creditLimit().subtract(card.availableLimit());
                        log.debug("Credit card balance - used: {}, available: {}", usedLimit, card.availableLimit());
                        return Mono.just(new CardBalance(card.id(), card.type(), "PEN",
                                                        usedLimit, card.availableLimit()));
                    }
                })
                .doOnSuccess(balance -> log.info("Successfully retrieved balance for card: {}", cardId.value()))
                .doOnError(error -> log.error("Failed to get balance for card: {}", cardId.value(), error));
    }

    public record CardBalance(
        CardId cardId,
        Card.CardType type,
        String currency,
        BigDecimal balance,
        BigDecimal availableBalance
    ) {}
}