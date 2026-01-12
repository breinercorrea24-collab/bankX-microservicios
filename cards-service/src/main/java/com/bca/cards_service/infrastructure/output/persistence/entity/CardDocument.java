package com.bca.cards_service.infrastructure.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "cards")
public record CardDocument(
        @Id String id,
        String cardId,
        String type,
        String customerId,
        String maskedNumber,
        String status,
        BigDecimal creditLimit,
        BigDecimal availableLimit,
        String linkedAccountId,
        LocalDateTime createdAt) {

}
