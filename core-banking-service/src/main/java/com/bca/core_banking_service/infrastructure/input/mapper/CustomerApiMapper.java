package com.bca.core_banking_service.infrastructure.input.mapper;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountPolymorphicResponse;
import com.bca.core_banking_service.dto.AccountType;
import com.bca.core_banking_service.dto.CheckingAccountResponse;
import com.bca.core_banking_service.dto.FixedTermAccountResponse;
import com.bca.core_banking_service.dto.SavingsAccountResponse;


public class CustomerApiMapper {
      public static AccountPolymorphicResponse toPolymorphicResponse(Account account) {

        return switch (account.getType()) {

            case SAVINGS -> toSavingsResponse((SavingsAccount) account);

            case CHECKING -> toCheckingResponse((CheckingAccount) account);

            case FIXED_TERM -> toFixedTermResponse((FixedTermAccount) account);

            default -> throw new IllegalArgumentException("Unsupported account type: " + account.getType());
        };
    }

    private static SavingsAccountResponse toSavingsResponse(SavingsAccount acc) {
        if (acc == null) {
            throw new IllegalArgumentException("SavingsAccount cannot be null");
        }
        return new SavingsAccountResponse()
                .id(acc.getId())
                .customerId(acc.getCustomerId())
                .type(AccountType.SAVINGS)
                .currency(acc.getCurrency())
                .balance(acc.getBalance() != null ? acc.getBalance().floatValue() : 0.0f)
                .status(acc.getStatus() != null ? SavingsAccountResponse.StatusEnum.valueOf(acc.getStatus().name()) : SavingsAccountResponse.StatusEnum.INACTIVE)
                .maxMonthlyTransactions(acc.getMaxMonthlyTransactions())
                .currentTransactions(acc.getCurrentTransactions())
                .maintenanceCommission(acc.getMaintenanceCommission() != null ? acc.getMaintenanceCommission() : BigDecimal.ZERO);
    }

    private static CheckingAccountResponse toCheckingResponse(CheckingAccount acc) {
        return new CheckingAccountResponse()
                .id(acc.getId())
                .customerId(acc.getCustomerId())
                .type(AccountType.CHECKING)
                .currency(acc.getCurrency())
                .balance(acc.getBalance() != null ? acc.getBalance().floatValue() : 0.0f)
                .status(CheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(acc.getMaxMonthlyTransactions())
                .maintenanceCommission(acc.getMaintenanceCommission());
    }

    private static FixedTermAccountResponse toFixedTermResponse(FixedTermAccount acc) {
        return new FixedTermAccountResponse()
                .id(acc.getId())
                .customerId(acc.getCustomerId())
                .type(AccountType.FIXED_TERM)
                .currency(acc.getCurrency())
                .balance(acc.getBalance() != null ? acc.getBalance().floatValue() : 0.0f)
                .status(FixedTermAccountResponse.StatusEnum.ACTIVE)
                .allowedDay(acc.getAllowedDay())
                .interestRate(acc.getInterestRate())
                .maintenanceFeeFree(acc.isMaintenanceFeeFree())
                .allowedMovementDay(acc.getAllowedMovementDay())
                .movementsThisMonth(acc.getMovementsThisMonth());
    }
}
