package com.bca.core_banking_service.application.usecases.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.bca.core_banking_service.application.usecases.validation.BusinessAccountExtension;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;

@Component
public class AccountFactory {

    public static Account create(CreateAccountCommand cmd) {

        Account account;

        // ...existing code...
        switch (cmd.getType()) {

            case SAVINGS:
                account = new SavingsAccount(
                        cmd.getCustomerId(),
                        cmd.getCurrency(),
                        ProductStatus.ACTIVE,
                        AccountType.SAVINGS,
                        5,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);
                break;

            case CHECKING:
                account = new CheckingAccount(
                        cmd.getCustomerId(),
                        cmd.getCurrency(),
                        ProductStatus.ACTIVE,
                        AccountType.CHECKING,
                        5,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);
                break;

            case FIXED_TERM:
                account = new FixedTermAccount(
                        cmd.getCustomerId(),
                        cmd.getCurrency(),
                        ProductStatus.ACTIVE,
                        AccountType.FIXED_TERM,
                        BigDecimal.valueOf(4.5),
                        true,
                        14,
                        1,
                        BigDecimal.ZERO);
                break;

            case VIP_SAVINGS:
                account = new VipSavingsAccount(
                        cmd.getCustomerId(),
                        cmd.getCurrency(),
                        ProductStatus.ACTIVE,
                        AccountType.VIP_SAVINGS,
                        5,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("5000"));
                break;

            case PYME_CHECKING:
                account = new PymeCheckingAccount(
                        cmd.getCustomerId(),
                        cmd.getCurrency(),
                        ProductStatus.ACTIVE,
                        AccountType.PYME_CHECKING,
                        Integer.MAX_VALUE,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);
                break;

            default:
                throw new BusinessException("Invalid account type");
        }

        /* ========= BUSINESS EXTENSION ========= */

        if (isBusinessType(cmd.getCustomerType())) {

            BusinessAccountExtension extension = new BusinessAccountExtension(
                    cmd.getHolders(),
                    cmd.getAuthorizedSigners());

            extension.validateBusinessRules();

            account.attachBusinessExtension(extension);
        }

        return account;
        // ...existing code...

    }

    private static boolean isBusinessType(CustomerType customerType) {

        return customerType == CustomerType.BUSINESS ||
                customerType == CustomerType.PYMEBUSINESS;
    }
}
