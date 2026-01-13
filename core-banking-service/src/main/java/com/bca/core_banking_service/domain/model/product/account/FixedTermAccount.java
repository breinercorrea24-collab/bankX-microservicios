package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FixedTermAccount extends Account {

    private int allowedDay;
    private BigDecimal interestRate;        // Tasa de interés
    private boolean maintenanceFeeFree;     // Siempre true (sin comisión)
    private Integer allowedMovementDay;     // Día del mes permitido (ej: 15)

    private Integer movementsThisMonth;     // Control de 1 movimiento mensual


    public FixedTermAccount(String customerId, String currency, BigDecimal interestRate, boolean maintenanceFeeFree, 
        Integer allowedMovementDay, Integer movementsThisMonth,BigDecimal initialDeposit) {
        // super(customerId, currency, 0);
        this.interestRate = interestRate;
        this.maintenanceFeeFree = maintenanceFeeFree;
        this.allowedMovementDay = allowedMovementDay;
        this.movementsThisMonth = movementsThisMonth;
        this.balance = initialDeposit;
    }

    @Override
    public boolean hasMaintenanceFee() {
        return false;
    }

    public void validateOperationDate(LocalDate date){
        if(date.getDayOfMonth() != allowedDay){
            throw new RuntimeException("Operación no permitida hoy");
        }
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
