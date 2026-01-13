package com.bca.core_banking_service.infrastructure.input.mapper;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.dto.AccountType;
import com.bca.core_banking_service.dto.TransactionResponse;

public class AccountApiMapper {

    public static AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setType(AccountType.valueOf(account.getType().name()));
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance().floatValue());
        response.setStatus(AccountResponse.StatusEnum.valueOf(account.getStatus().name()));
        return response;
    }

    public static TransactionResponse mapToTransactionResponse(Account account, String type, BigDecimal amount) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId("tx-" + System.currentTimeMillis());
        response.setAccountId(account.getId());
        response.setType(TransactionResponse.TypeEnum.valueOf(type));
        response.setAmount(amount.floatValue());
        response.setBalance(account.getBalance().floatValue());
        response.setTimestamp(java.time.OffsetDateTime.now());
        return response;
    }
}
