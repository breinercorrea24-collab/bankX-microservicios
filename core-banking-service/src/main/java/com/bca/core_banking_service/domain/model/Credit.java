package com.bca.core_banking_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private CreditType creditType;
    private BigDecimal originalAmount;
    private BigDecimal pendingDebt;
    private BigDecimal interestRate;
    private Integer termMonths;
    private CreditStatus status;
    private LocalDateTime createdAt;

    public enum CreditType {
        PERSONAL_LOAN, MORTGAGE, AUTO_LOAN
    }

    public enum CreditStatus {
        ACTIVE, PAID, DEFAULTED
    }
}