package com.bca.wallets_service.domain.ports.output;

import com.bca.wallets_service.domain.model.DebitCard;
import reactor.core.publisher.Mono;

public interface DebitCardRepository {
    Mono<DebitCard> findById(String debitCardId);
}