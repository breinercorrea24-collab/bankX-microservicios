package com.bca.core_banking_service.application.usecases.factory;

import java.util.List;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateAccountCommand {
    private String customerId;
    private CustomerType customerType;
    private AccountType type;
    private String currency;

    private List<String> holders;
    private List<String> authorizedSigners;

    public boolean isBusiness() {
        return customerType == CustomerType.BUSINESS
            || customerType == CustomerType.PYMEBUSINESS;
    }

    public CreateAccountCommand(String customerId, CustomerType customerType, AccountType type, String currency) {
        this.customerId = customerId;
        this.customerType = customerType;
        this.type = type;
        this.currency = currency;
    }

}
