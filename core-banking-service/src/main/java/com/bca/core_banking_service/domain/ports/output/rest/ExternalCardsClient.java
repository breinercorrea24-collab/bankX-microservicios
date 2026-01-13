package com.bca.core_banking_service.domain.ports.output.rest;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public interface ExternalCardsClient {
    Mono<ResponseEntity<Boolean>> hasCreditCard(String customerId);
}
