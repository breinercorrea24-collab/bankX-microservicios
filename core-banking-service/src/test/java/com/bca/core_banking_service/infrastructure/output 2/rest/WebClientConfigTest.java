package com.bca.core_banking_service.infrastructure.output.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class WebClientConfigTest {

    private final WebClientConfig config = new WebClientConfig();

    @Test
    void webClientBuilderCreatesNewBuilder() {
        WebClient.Builder builder = config.webClientBuilder();
        assertNotNull(builder);
        assertNotNull(builder.build());
    }
}
