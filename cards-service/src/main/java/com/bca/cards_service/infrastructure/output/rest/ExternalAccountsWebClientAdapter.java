package com.bca.cards_service.infrastructure.output.rest;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.ports.rest.ExternalAccountsClient;
import com.bca.cards_service.infrastructure.output.rest.dto.AccountBalanceResponse;
import com.bca.cards_service.infrastructure.output.rest.dto.AmountRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalAccountsWebClientAdapter implements ExternalAccountsClient {
    private final WebClient.Builder webClientBuilder;
    private final AccountsServiceProperties properties;


    @Override
    public Mono<ResponseEntity<AccountBalanceResponse>> AccountWithdrawal(String accountId, BigDecimal amount) {

        AmountRequest request = new AmountRequest(amount);

        return webClientBuilder.build()
                .post()
                .uri(uriBuilder -> {
                    uriBuilder
                            .scheme("http")
                            .host(properties.getServiceId())
                            .path(properties.getEndpoints().getWithdraw());

                    if (properties.getPort() != null) {
                        uriBuilder.port(properties.getPort());
                    }

                    return uriBuilder.build(accountId);
                })
                .bodyValue(request)
                .retrieve()
                .toEntity(AccountBalanceResponse.class);
    }

    @Override
    public Mono<ResponseEntity<AccountBalanceResponse>> AccountBalance(String accountId) {
        
        if (accountId == null || accountId.trim().isEmpty()) {
            return Mono.error(new BusinessException("accountId is required"));
        }
        String safeId = accountId.trim();
        
        return webClientBuilder.build()
            .get()
            .uri(uriBuilder -> {
                uriBuilder
                    .scheme("http")
                    .host(properties.getServiceId())
                    .path(properties.getEndpoints().getBalance());

                if (properties.getPort() != null) {
                    uriBuilder.port(properties.getPort());
                }

                return uriBuilder.build(Collections.singletonMap("accountId", safeId));

            })
            .retrieve()
            .toEntity(AccountBalanceResponse.class)
            .doOnSubscribe(s -> log.info("Calling accountId={}", accountId))
            .doOnError(e -> log.error("WebClient error", e));
    }
}
