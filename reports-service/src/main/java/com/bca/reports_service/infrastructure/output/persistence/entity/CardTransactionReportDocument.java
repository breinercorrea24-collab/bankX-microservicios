package com.bca.reports_service.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transaction_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionReportDocument {
    @Id
    private String eventId;

    private String cardId;
    private String cardType;
    private String maskedNumber;

    private String customerId;

    private String movementType;
    private Double amount;
    private String currency;

    private String merchant;
    private String status;

    private Instant transactionDate;
}
