package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckingAccount extends Account {

    private BigDecimal maintenanceCommission;
    private int maxMonthlyTransactions;

    public CheckingAccount(String customerId, String currency, ProductStatus status, AccountType type,
            int maxMonthlyTransactions, BigDecimal maintenanceCommission,
            BigDecimal balance) {
        super(customerId, currency, status, balance); // Llamada al constructor de Account
        this.type = type;
        this.maxMonthlyTransactions = maxMonthlyTransactions;
        this.maintenanceCommission = maintenanceCommission;
    }


    public void chargeMaintenanceFee() {
        this.balance = this.balance.subtract(maintenanceCommission);
    }

    @Override
    public void deposit(BigDecimal amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deposit'");
    }

    @Override
    public void withdraw(BigDecimal amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }

    @Override
    public boolean hasMaintenanceFee() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasMaintenanceFee'");
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}