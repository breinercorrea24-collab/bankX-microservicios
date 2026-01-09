package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.model.AccountId;
import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkDebitCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> execute(CardId cardId, AccountId accountId) {
        log.info("Executing LinkDebitCardUseCase for card: {} to account: {}", cardId.value(), accountId.value());
        return cardRepository.findById(cardId)
                .flatMap(card -> {
                    if (!card.isDebit()) {
                        log.warn("Attempted to link non-debit card: {}", cardId.value());
                        return Mono.error(new IllegalArgumentException("Only debit cards can be linked to accounts"));
                    }
                    if (card.linkedAccountId() != null) {
                        log.warn("Card {} is already linked to account {}", cardId.value(), card.linkedAccountId().value());
                        return Mono.error(new IllegalArgumentException("Card is already linked to an account"));
                    }
                    Card linkedCard = card.linkToAccount(accountId);
                    return cardRepository.save(linkedCard);
                })
                .doOnSuccess(card -> log.info("Successfully linked card {} to account {}", cardId.value(), accountId.value()))
                .doOnError(error -> log.error("Failed to link card {} to account {}", cardId.value(), accountId.value(), error));
    }
}