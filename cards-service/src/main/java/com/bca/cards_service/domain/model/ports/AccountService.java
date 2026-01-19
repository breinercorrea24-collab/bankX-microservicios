package com.bca.cards_service.domain.model.ports;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountService {
    Mono<BigDecimal> getAccountBalance(String accountId);
}