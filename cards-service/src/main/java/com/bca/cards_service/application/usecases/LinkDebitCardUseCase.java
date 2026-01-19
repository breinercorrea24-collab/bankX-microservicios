package com.bca.cards_service.application.usecases;

import com.bca.cards_service.application.usecases.factory.CardFactory;
import com.bca.cards_service.application.usecases.factory.CreateCardCommand;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkDebitCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> execute(String cardId, String accountId) {
        log.info("Executing LinkDebitCardUseCase for card: {} to account: {}", cardId, accountId);

        return cardRepository.findById(cardId)
                .flatMap(card -> {

                    if (card.getType() != CardType.DEBIT) {
                        log.warn("Attempted to link non-debit card: {}", cardId);
                        return Mono.error(new BusinessException("Only debit cards can be linked to accounts"));
                    }

                    Card cardSaved = CardFactory
                            .create(new CreateCardCommand(card.getCustomerId(), accountId, CardType.DEBIT, BigDecimal.ZERO));
                    DebitCard debitCard = (DebitCard) card;
                    Set<String> linkedAccountIds = debitCard.getLinkedAccountIds();
                    if (linkedAccountIds.isEmpty()) {
                        debitCard.setPrimaryAccountId(accountId);
                    }

                    linkedAccountIds.add(accountId);

                    debitCard.setLinkedAccountIds(linkedAccountIds);
                    cardSaved = debitCard;

                    // Card linkedCard = card.linkToAccount(accountId);
                    return cardRepository.save(cardSaved);
                })
                .doOnSuccess(c -> log.info("Successfully linked card {} to account {}", cardId, accountId))
                .doOnError(error -> log.error("Failed to link card {} to account {}", cardId, accountId, error));
    }
}