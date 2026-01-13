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
public class VipSavingsAccount extends SavingsAccount {
    private BigDecimal minimumDailyAverage;

    public VipSavingsAccount(String customerId, String currency, ProductStatus active, AccountType vipSavings,
            int maxMonthlyTransactions,
            BigDecimal zero, BigDecimal zero2, BigDecimal bigDecimal) {
        super(customerId, currency, active, vipSavings, maxMonthlyTransactions, zero, zero2);
        this.minimumDailyAverage = bigDecimal;
    }

}