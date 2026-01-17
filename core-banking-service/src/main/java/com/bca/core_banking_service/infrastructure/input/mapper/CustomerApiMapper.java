package com.bca.core_banking_service.infrastructure.input.mapper;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;
import com.bca.core_banking_service.dto.AccountPolymorphicResponse;
import com.bca.core_banking_service.dto.AccountType;
import com.bca.core_banking_service.dto.CheckingAccountResponse;
import com.bca.core_banking_service.dto.FixedTermAccountResponse;
import com.bca.core_banking_service.dto.PymeCheckingAccountResponse;
import com.bca.core_banking_service.dto.SavingsAccountResponse;
import com.bca.core_banking_service.dto.VipSavingsAccountResponse;

public class CustomerApiMapper {

    private CustomerApiMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static AccountPolymorphicResponse toPolymorphicResponse(Account account) {
        if (account == null || account.getType() == null) {
            throw new IllegalArgumentException("Unsupported account type: null");
        }

        return switch (account.getType()) {

            case SAVINGS -> toSavingsResponse((SavingsAccount) account);

            case CHECKING -> toCheckingResponse((CheckingAccount) account);

            case FIXED_TERM -> toFixedTermResponse((FixedTermAccount) account);

            case VIP_SAVINGS -> toVipSavingsResponse((VipSavingsAccount) account);

            case PYME_CHECKING -> toPymeCheckingAccountResponse((PymeCheckingAccount) account);

            default -> throw new IllegalArgumentException("Unsupported account type: " + account.getType());
        };
    }

    private static VipSavingsAccountResponse toVipSavingsResponse(VipSavingsAccount acc) {
        if (acc == null) {
            throw new IllegalArgumentException("VipSavingsAccount cannot be null");
        }
        return new VipSavingsAccountResponse()
                .id(acc.getId())
                .customerId(acc.getCustomerId())
                .type(AccountType.SAVINGS)
                .currency(acc.getCurrency())
                .balance(acc.getBalance() != null ? acc.getBalance().floatValue() : 0.0f)
                .status(acc.getStatus() != null ? VipSavingsAccountResponse.StatusEnum.valueOf(acc.getStatus().name()) : VipSavingsAccountResponse.StatusEnum.INACTIVE)
                .maxMonthlyTransactions(acc.getMaxMonthlyTransactions())
                .currentTransactions(acc.getCurrentTransactions())
                .maintenanceCommission(acc.getMaintenanceCommission() != null ? acc.getMaintenanceCommission() : BigDecimal.ZERO)
                .minimumDailyAverage(acc.getMinimumDailyAverage() != null ? acc.getMinimumDailyAverage() : BigDecimal.ZERO);
    }

    private static PymeCheckingAccountResponse toPymeCheckingAccountResponse(PymeCheckingAccount acc) {
        return new PymeCheckingAccountResponse()
                .id(acc.getId())
                .customerId(acc.getCustomerId())
                .type(AccountType.CHECKING)
                .currency(acc.getCurrency())
                .balance(acc.getBalance() != null ? acc.getBalance().floatValue() : 0.0f)
                .status(PymeCheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(acc.getMaxMonthlyTransactions())
                .maintenanceCommission(acc.getMaintenanceCommission());
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
        if (acc == null) {
            throw new IllegalArgumentException("CheckingAccount cannot be null");
        }
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
        if (acc == null) {
            throw new IllegalArgumentException("FixedTermAccount cannot be null");
        }
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
