package com.bca.core_banking_service.domain.model.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BusinessLoanCredit extends Credit {

    public BusinessLoanCredit(
            String customerId,
            BigDecimal amount,
            BigDecimal interestRate,
            Integer termMonths
    ) {
        super(
            null,
            customerId,
            CreditType.BUSINESS_LOAN,
            amount, // originalAmount
            amount, // pendingDebt (initially equal to original amount)
            interestRate,
            amount, // creditLimit (for loans limit equals amount)
            termMonths,
            LocalDateTime.now(),
            LocalDateTime.now().plusMonths(termMonths),
            CreditStatus.ACTIVE
        );
    }

    public BigDecimal calculateInterest() { 
        // Ejemplo simple de cálculo de interés
        return this.originalAmount.multiply(this.interestRate).divide(BigDecimal.valueOf(100));
    }
}