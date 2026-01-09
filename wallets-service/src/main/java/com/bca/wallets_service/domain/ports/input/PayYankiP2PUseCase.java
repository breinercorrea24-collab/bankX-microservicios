package com.bca.wallets_service.domain.ports.input;

import com.bca.wallets_service.domain.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PayYankiP2PUseCase {
    Mono<Transaction> pay(String fromId, String toId, BigDecimal amount);
}