package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

import com.bca.core_banking_service.application.usecases.validation.BusinessAccountExtension;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.Product;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Account extends Product {

    protected String accountNumber;
    protected BigDecimal balance;
    protected String currency;
    protected AccountType type;

    protected int freeTransactionLimit;
    protected BigDecimal commissionPerExtra;
    protected BigDecimal initialDeposit;

    public enum AccountStatus {
        ACTIVE, INACTIVE
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        balance = balance.subtract(amount);
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Monto inválido");
        }
    }

    public abstract boolean hasMaintenanceFee();

    public Account(String customerId, String currency, ProductStatus status, BigDecimal balance) {
        super(null, customerId, status);
        this.currency = currency;
        this.balance = balance;
    }

    protected CustomerType customerType;
    protected BusinessAccountExtension businessData;

     public boolean isBusiness() {
        return customerType == CustomerType.BUSINESS
            || customerType == CustomerType.PYMEBUSINESS;
    }

    /* ========= VALIDACIÓN GLOBAL ========= */

    public void attachBusinessExtension(BusinessAccountExtension ext) {

        if (!isBusiness()) {
            throw new BusinessException(
                "Cannot assign business data to non business account");
        }

        this.businessData = ext;
        ext.validateBusinessRules(); // 🔥 aquí validas TODO
    }

}
