package com.bca.cards_service.infrastructure.output.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.accounts")
public class AccountsServiceProperties {
    private String serviceId;
    private Integer port;
    private Endpoints endpoints;

    @Data
    public static class Endpoints {
        private String withdraw;
        private String balance;
    }
}
