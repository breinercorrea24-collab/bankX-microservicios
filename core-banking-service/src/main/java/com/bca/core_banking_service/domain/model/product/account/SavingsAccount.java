package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsAccount extends Account {

    private int maxMonthlyTransactions;
    private int currentTransactions;
    private BigDecimal maintenanceCommission;

    public SavingsAccount(String customerId, String currency, ProductStatus status, AccountType type,
            int maxMonthlyTransactions, BigDecimal maintenanceCommission,
            BigDecimal balance) {

        super(customerId, currency, status, balance); // Llamada al constructor de Account
        this.type = type;
        this.maxMonthlyTransactions = maxMonthlyTransactions;
        this.maintenanceCommission = maintenanceCommission;
    }

    @Override
    public boolean hasMaintenanceFee() {
        return false;
    }

    public void validateMonthlyLimit() {
        if (currentTransactions >= maxMonthlyTransactions) {
            throw new RuntimeException("Límite mensual alcanzado");
        }
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
