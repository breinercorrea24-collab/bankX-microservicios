package com.bca.reports_service.infrastructure.output.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "commission_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionEventDocument {
    @Id
    private String id;           // evt-comm-001
    
    private String productType;  // BANK_ACCOUNT
    private String customerId;   // cust-001
    private String productId;    // acc-001
    
    private String accountType;  // CURRENT
    private String commissionType; // MAINTENANCE
    private Double amount;
    private String currency;
    private Instant chargedAt;
}