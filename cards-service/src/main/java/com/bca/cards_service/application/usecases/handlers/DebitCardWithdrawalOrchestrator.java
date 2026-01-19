package com.bca.cards_service.application.usecases.handlers;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.rest.ExternalAccountsClient;
import com.bca.cards_service.infrastructure.output.rest.dto.AccountBalanceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebitCardWithdrawalOrchestrator {
    private final ExternalAccountsClient accountsClient;

    public Mono<Void> withdrawCascade(
            DebitCard card,
            BigDecimal totalAmount) {

        AtomicReference<BigDecimal> remaining = new AtomicReference<>(totalAmount);

        return Flux.fromIterable(card.getWithdrawalOrder())
                .concatMap(accountId -> withdrawFromAccount(accountId, remaining))
                .then(validateRemainingBalance(remaining));
    }

    private Mono<Void> withdrawFromAccount(
            String accountId,
            AtomicReference<BigDecimal> remaining) {

        return accountsClient.AccountBalance(accountId)
                .map(response -> response != null ? response.getBody() : null)
                // .defaultIfEmpty(null)
                .flatMap(account -> {

                    BigDecimal balance = getSafeBalance(account);
                    BigDecimal rest = remaining.get();

                    if (noWithdrawalNeeded(rest, balance)) {
                        return Mono.<Void>empty();
                    }

                    BigDecimal toWithdraw = balance.min(rest);
                    remaining.set(rest.subtract(toWithdraw));

                    return accountsClient.AccountWithdrawal(accountId, toWithdraw).then();
                });
    }

    private BigDecimal getSafeBalance(AccountBalanceResponse account) {
        return account != null && account.getBalance() != null
                ? account.getBalance()
                : BigDecimal.ZERO;
    }

    private boolean noWithdrawalNeeded(
            BigDecimal remaining,
            BigDecimal balance) {
        return remaining.signum() <= 0 || balance.signum() <= 0;
    }

    private Mono<Void> validateRemainingBalance(
            AtomicReference<BigDecimal> remaining) {

        return Mono.defer(() -> {
            if (remaining.get().signum() > 0) {
                return Mono.error(
                        new BusinessException("Insufficient balance"));
            }
            return Mono.empty();
        });
    }
}
