package com.bca.cards_service.application.usecases.factory;

import java.math.BigDecimal;

import com.bca.cards_service.domain.enums.card.CardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateCardCommand {
    private String customerId;
    private String accountId;
    private CardType cardType;
    private BigDecimal creditLimit;
}
