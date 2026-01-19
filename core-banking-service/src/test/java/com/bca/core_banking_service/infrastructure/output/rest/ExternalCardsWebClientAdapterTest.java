package com.bca.core_banking_service.infrastructure.output.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

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
        endpoints.setHasCreditCard("/external/cards/{customerId}/credit");
        properties.setEndpoints(endpoints);

        adapter = new ExternalCardsWebClientAdapter(WebClient.builder(), properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void hasCreditCard_callsConfiguredEndpointAndReturnsResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("true"));

        StepVerifier.create(adapter.hasCreditCard("cust-123"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(Boolean.TRUE.equals(response.getBody()));
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/external/cards/cust-123/credit", request.getPath());
    }
}
