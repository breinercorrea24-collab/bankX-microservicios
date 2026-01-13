package com.bca.core_banking_service.infrastructure.output.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.cards")
public class CardsServiceProperties {

    private String serviceId;
    private Endpoints endpoints;

    @Data
    public static class Endpoints {
        private String hasCreditCard;
    }
}