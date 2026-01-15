package com.bca.core_banking_service.domain.model.credit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PersonalLoanCredit extends Credit {

    public PersonalLoanCredit(
            String customerId,
            BigDecimal amount,
            BigDecimal interestRate,
            Integer termMonths
    ) {
        super(
            null, // id se asigna al persistir
            customerId,
            CreditType.PERSONAL_LOAN,
            amount, // originalAmount
            amount, // pendingDebt (inicial igual al monto)
            interestRate,
            amount, // creditLimit (para préstamos, límite = monto)
            termMonths,
            LocalDateTime.now(),
            LocalDateTime.now().plusMonths(termMonths),
            CreditStatus.ACTIVE
        );
    }

    public BigDecimal calculateInstallment() { 
        // Cálculo simple de cuota mensual (sin considerar amortización)
        BigDecimal monthlyInterestRate = this.interestRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        BigDecimal numerator = monthlyInterestRate.multiply(this.originalAmount);
        BigDecimal base = BigDecimal.ONE.add(monthlyInterestRate);
        int n = this.termMonths == null ? 0 : this.termMonths;
        if (n <= 0) {
            throw new IllegalArgumentException("termMonths must be positive");
        }
        BigDecimal pow = base.pow(n);
        BigDecimal inverse = BigDecimal.ONE.divide(pow, 10, RoundingMode.HALF_UP);
        BigDecimal denominator = BigDecimal.ONE.subtract(inverse);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
     }
}