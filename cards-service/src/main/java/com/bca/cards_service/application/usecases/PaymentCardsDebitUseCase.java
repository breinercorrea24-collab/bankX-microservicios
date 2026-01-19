package com.bca.cards_service.application.usecases;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.bca.cards_service.application.usecases.handlers.DebitCardWithdrawalOrchestrator;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.CardRepository;
import com.bca.cards_service.domain.model.ports.rest.ExternalAccountsClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCardsDebitUseCase {

    private final CardRepository cardRepository;
    private final ExternalAccountsClient accountsClient;


    public Mono<Void> pay(String cardId, BigDecimal amount) {

        DebitCardWithdrawalOrchestrator orchestrator = new DebitCardWithdrawalOrchestrator(accountsClient);

        return cardRepository.findById(cardId)
                .flatMap(card -> {

                    if (!card.isActive()) {
                        return Mono.error(
                                new BusinessException("Debit card blocked"));
                    }

                    if (!(card instanceof DebitCard)) {
                        throw new BusinessException("Card is not a debit card");
                    }

                    return orchestrator.withdrawCascade(
                            (DebitCard) card,
                            amount);
                });
    }

    public Mono<Void> withdraw(String cardId, BigDecimal amount) {
        
        DebitCardWithdrawalOrchestrator orchestrator = new DebitCardWithdrawalOrchestrator(accountsClient);

        
        return cardRepository.findById(cardId)
                .flatMap(card -> {

                    if (!card.isActive()) {
                        return Mono.error(
                                new BusinessException("Debit card blocked"));
                    }

                    if (!(card instanceof DebitCard)) {
                        throw new BusinessException("Card is not a debit card");
                    }

                    return orchestrator.withdrawCascade(
                            (DebitCard) card,
                            amount);
                });
    } 

}
