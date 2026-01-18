package com.bca.core_banking_service.infrastructure.output.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    void equalsAndHashCodeIncludeEndpoints() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setServiceId("service");
        first.setPort(8080);
        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/endpoint");
        first.setEndpoints(endpoints);

        CardsServiceProperties second = new CardsServiceProperties();
        second.setServiceId("service");
        second.setPort(8080);
        CardsServiceProperties.Endpoints secondEndpoints = new CardsServiceProperties.Endpoints();
        secondEndpoints.setHasCreditCard("/endpoint");
        second.setEndpoints(secondEndpoints);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());

        secondEndpoints.setHasCreditCard("/different");
        assertNotEquals(first, second);
    }

    @Test
    void equalsHandlesNullValues() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setPort(null);
        first.setServiceId(null);
        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard(null);
        first.setEndpoints(endpoints);

        CardsServiceProperties second = new CardsServiceProperties();
        second.setPort(null);
        second.setServiceId(null);
        CardsServiceProperties.Endpoints secondEndpoints = new CardsServiceProperties.Endpoints();
        secondEndpoints.setHasCreditCard(null);
        second.setEndpoints(secondEndpoints);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "other");
    }

    @Test
    void equalsReturnsTrueForSameInstance() {
        CardsServiceProperties properties = new CardsServiceProperties();
        properties.setServiceId("cards");
        properties.setPort(9090);
        assertEquals(properties, properties);
    }

    @Test
    void equalsDetectsWhenServiceIdDiffers() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setServiceId("service-a");
        first.setPort(8080);
        CardsServiceProperties second = new CardsServiceProperties();
        second.setServiceId("service-b");
        second.setPort(8080);

        assertNotEquals(first, second);
    }

    @Test
    void equalsDetectsWhenPortDiffers() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setServiceId("service");
        first.setPort(8080);
        CardsServiceProperties second = new CardsServiceProperties();
        second.setServiceId("service");
        second.setPort(9090);

        assertNotEquals(first, second);
    }

    @Test
    void equalsHandlesNullEndpointsAgainstValue() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setServiceId("service");
        first.setPort(8080);
        first.setEndpoints(null);

        CardsServiceProperties second = new CardsServiceProperties();
        second.setServiceId("service");
        second.setPort(8080);
        CardsServiceProperties.Endpoints endpoints = new CardsServiceProperties.Endpoints();
        endpoints.setHasCreditCard("/foo");
        second.setEndpoints(endpoints);

        assertNotEquals(first, second);
    }

    @Test
    void endpointsEqualityUsesHasCreditCardField() {
        CardsServiceProperties.Endpoints first = new CardsServiceProperties.Endpoints();
        first.setHasCreditCard("/cards/{customerId}/credit-card");
        CardsServiceProperties.Endpoints second = new CardsServiceProperties.Endpoints();
        second.setHasCreditCard("/cards/{customerId}/credit-card");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        second.setHasCreditCard("/different");
        assertNotEquals(first, second);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }

    @Test
    void equalsChecksDifferentInstanceTypes() {
        CardsServiceProperties first = new CardsServiceProperties();
        first.setServiceId("service");
        first.setPort(8080);

        assertNotEquals(first, new Object());
    }

    @Test
    void endpointsEqualityTreatsDifferentTypesAsUnequal() {
        CardsServiceProperties.Endpoints first = new CardsServiceProperties.Endpoints();
        first.setHasCreditCard("/cards/{customerId}/credit-card");
        assertNotEquals(first, new Object());
    }

    @Test
    void endpointsEqualityHandlesNullValues() {
        CardsServiceProperties.Endpoints first = new CardsServiceProperties.Endpoints();
        first.setHasCreditCard(null);
        CardsServiceProperties.Endpoints second = new CardsServiceProperties.Endpoints();
        second.setHasCreditCard(null);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setHasCreditCard("/cards/{customerId}/credit-card");
        assertNotEquals(first, second);
    }
}
