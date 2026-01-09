package com.bca.core_banking_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private String customerId;
    private AccountType type;
    private String currency;
    private BigDecimal balance;
    private AccountStatus status;

    public enum AccountType {
        SAVINGS, CURRENT
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE
    }
}