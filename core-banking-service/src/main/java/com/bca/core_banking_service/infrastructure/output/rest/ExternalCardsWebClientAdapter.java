package com.bca.core_banking_service.infrastructure.output.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bca.core_banking_service.domain.ports.output.rest.ExternalCardsClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ExternalCardsWebClientAdapter
        implements ExternalCardsClient {

    private final WebClient.Builder webClientBuilder;
    private final CardsServiceProperties properties;

    @Override
    public Mono<ResponseEntity<Boolean>> hasCreditCard(String customerId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(properties.getServiceId())
                        .path(properties.getEndpoints().getHasCreditCard())
                        .build(customerId))
                .retrieve()
                .toEntity(Boolean.class);
    }
}