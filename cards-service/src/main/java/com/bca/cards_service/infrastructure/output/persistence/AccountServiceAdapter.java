package com.bca.cards_service.infrastructure.output.persistence;

import com.bca.cards_service.domain.model.ports.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
public class AccountServiceAdapter implements AccountService {

    @Override
    public Mono<BigDecimal> getAccountBalance(String accountId) {
        log.info("Fetching balance for account: {}", accountId);
        // Mock implementation - in real world, this would call an external account service
        // For demo purposes, return a fixed balance
        BigDecimal balance = BigDecimal.valueOf(1700.00);
        log.debug("Account balance retrieved: {} for account: {}", balance, accountId);
        return Mono.just(balance)
                .doOnError(error -> log.error("Failed to get balance for account: {}", accountId, error));
    }
}