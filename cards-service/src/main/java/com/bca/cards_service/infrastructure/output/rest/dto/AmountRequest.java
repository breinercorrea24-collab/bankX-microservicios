package com.bca.cards_service.infrastructure.output.rest.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AmountRequest {
    private BigDecimal amount;

    public AmountRequest(BigDecimal amount) {
        this.amount = amount;
    }

}