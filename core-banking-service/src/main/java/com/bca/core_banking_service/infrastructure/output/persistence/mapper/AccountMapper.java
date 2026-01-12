package com.bca.core_banking_service.infrastructure.output.persistence.mapper;

import com.bca.core_banking_service.infrastructure.input.dto.Account;
import com.bca.core_banking_service.infrastructure.input.dto.Account.AccountStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Account.AccountType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.AccountEntity;

public class AccountMapper {

    public static Account toDomain(AccountEntity e) {
        return new Account(
            e.getId(),
            e.getCustomerId(),
            AccountType.valueOf(e.getType().name()), // Convert AccountEntity.AccountType to String
            e.getCurrency(),
            e.getBalance(),
            AccountStatus.valueOf(e.getStatus().name()), // Convert AccountEntity.AccountStatus to String
            e.isActive()
        );
    }

    public static AccountEntity toEntity(Account d) {
        AccountEntity e = new AccountEntity();
        e.setId(d.getId());
        e.setCustomerId(d.getCustomerId());
        e.setType(AccountEntity.AccountType.valueOf(d.getType().name())); // Convert Account.AccountType to AccountEntity.AccountType
        e.setCurrency(d.getCurrency());
        e.setBalance(d.getBalance());
        e.setStatus(AccountEntity.AccountStatus.valueOf(d.getStatus().name())); // Convert Account.AccountStatus to AccountEntity.AccountStatus
        return e;
    }
}
