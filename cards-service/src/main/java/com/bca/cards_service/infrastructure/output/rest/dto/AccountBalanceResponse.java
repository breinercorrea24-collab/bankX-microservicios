package com.bca.cards_service.infrastructure.output.rest.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountBalanceResponse {
    private String id;
    private BigDecimal balance;
}