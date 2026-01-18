package com.bca.core_banking_service.domain.model.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Credit {

    /* ===== Identidad ===== */
    protected String id;                 // Identidad del agregado
    protected String customerId;

    /* ===== Clasificación ===== */
    protected CreditType creditType;

    /* ===== Dinero ===== */
    protected BigDecimal originalAmount;
    protected BigDecimal pendingDebt;
    protected BigDecimal interestRate;
    protected BigDecimal creditLimit;

    /* ===== Plazo ===== */
    protected Integer termMonths;
    protected LocalDateTime createdAt;
    protected LocalDateTime dueDate;

    /* ===== Estado ===== */
    protected CreditStatus status;


    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Realiza un pago y reduce la deuda pendiente
     */
    public void makePayment(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (amount.compareTo(pendingDebt) > 0) {
            throw new IllegalArgumentException("Payment exceeds outstanding balance");
        }
        this.pendingDebt = this.pendingDebt.subtract(amount);
    }

    /**
     * Crédito disponible
     */
    public BigDecimal getAvailableCredit() {
        return creditLimit.subtract(pendingDebt);
    }
}
