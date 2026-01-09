package com.bca.wallets_service.infrastructure.output.persistence;

import com.bca.wallets_service.domain.model.DebitCard;
import com.bca.wallets_service.domain.ports.output.DebitCardRepository;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DebitCardRepositoryImpl implements DebitCardRepository {

    private final Map<String, DebitCard> debitCards = new HashMap<>();

    public DebitCardRepositoryImpl() {
        // Mock data
        debitCards.put("card-deb-789", new DebitCard("card-deb-789", "acc-001", "cus-1001"));
        debitCards.put("card-deb-790", new DebitCard("card-deb-790", "acc-002", "cus-1002"));
    }

    @Override
    public Mono<DebitCard> findById(String debitCardId) {
        DebitCard card = debitCards.get(debitCardId);
        return card != null ? Mono.just(card) : Mono.empty();
    }
}