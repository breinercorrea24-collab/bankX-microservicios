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
public class PymeCheckingAccount extends CheckingAccount {

    public PymeCheckingAccount(String customerId, String currency, ProductStatus status, AccountType type,
            int maxMonthlyTransactions, BigDecimal maintenanceCommission, BigDecimal balance) {
        super(customerId, currency, status, type, maxMonthlyTransactions, maintenanceCommission, balance);
    }

}