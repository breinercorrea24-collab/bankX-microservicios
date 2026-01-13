package com.bca.core_banking_service.infrastructure.input.mapper;

import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.dto.AccountResponse;


public class CustomerApiMapper {
    public static AccountResponse mapToAccountResponse(
            Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setType(AccountResponse.TypeEnum.valueOf(account.getType().name()));
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance().floatValue());
        response.setStatus(AccountResponse.StatusEnum.valueOf(account.getStatus().name()));
        return response;
    }
}
