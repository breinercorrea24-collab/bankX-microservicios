package com.bca.core_banking_service.infrastructure.output.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ExternalCardsWebClientAdapterTest {

    private MockWebServer mockWebServer;
    private ExternalCardsWebClientAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        CardsServiceProperties properties = new CardsServiceProperties();
        properties.setServiceId(mockWebServer.getHostName());
        properties.setPort(mockWebServer.getPort());
        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/cards/{customerId}/credit-card");
        properties.setEndpoints(endpoints);

        adapter = new ExternalCardsWebClientAdapter(WebClient.builder(), properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void hasCreditCard_whenServerReturnsTrue_emitsTrueResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("true"));

        StepVerifier.create(adapter.hasCreditCard("cust-123"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(Boolean.TRUE.equals(response.getBody()));
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/cards/cust-123/credit-card", request.getPath());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void hasCreditCard_whenServerReturnsError_emitsWebClientException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(adapter.hasCreditCard("cust-error"))
                .expectErrorSatisfies(error -> assertTrue(error instanceof WebClientResponseException))
                .verify();
    }

    @Test
    void hasCreditCard_whenPortNotConfigured_usesDefaultPort() {
        CardsServiceProperties properties = new CardsServiceProperties();
        properties.setServiceId("cards-service");
        properties.setPort(null);
        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/cards/{customerId}/credit-card");
        properties.setEndpoints(endpoints);

        final java.util.concurrent.atomic.AtomicReference<java.net.URI> capturedUri = new java.util.concurrent.atomic.AtomicReference<>();
        WebClient.Builder builder = WebClient.builder()
                .exchangeFunction(request -> {
                    capturedUri.set(request.url());
                    return Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header("Content-Type", "application/json")
                            .body("true")
                            .build());
                });

        ExternalCardsWebClientAdapter localAdapter = new ExternalCardsWebClientAdapter(builder, properties);

        StepVerifier.create(localAdapter.hasCreditCard("cust-42"))
                .assertNext(response -> assertEquals(Boolean.TRUE, response.getBody()))
                .verifyComplete();

        assertNotNull(capturedUri.get());
        assertEquals("cards-service", capturedUri.get().getHost());
        assertEquals(-1, capturedUri.get().getPort());
        assertEquals("/cards/cust-42/credit-card", capturedUri.get().getPath());
    }
}
