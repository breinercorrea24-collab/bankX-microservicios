package com.bca.core_banking_service.infrastructure.output.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CardsServicePropertiesTest {

    @Test
    void settersAndGettersWorkForServiceMetadata() {
        CardsServiceProperties properties = new CardsServiceProperties();
        properties.setServiceId("cards-service");
        properties.setPort(8085);

        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/cards/{customerId}/credit-card");
        properties.setEndpoints(endpoints);

        assertEquals("cards-service", properties.getServiceId());
        assertEquals(8085, properties.getPort());
        assertEquals("/cards/{customerId}/credit-card", properties.getEndpoints().getHasCreditCard());
    }

    @Test
    void endpointsDefaultsToNullUntilConfigured() {
        CardsServiceProperties properties = new CardsServiceProperties();
        assertNull(properties.getEndpoints());

        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/test");
        assertEquals("/test", endpoints.getHasCreditCard());
    }
}
