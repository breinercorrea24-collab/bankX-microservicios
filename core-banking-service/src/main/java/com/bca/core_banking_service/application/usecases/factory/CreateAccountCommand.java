package com.bca.core_banking_service.application.usecases.factory;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountCommand {
    private String customerId;
    private AccountType type;
    private String currency;
}
