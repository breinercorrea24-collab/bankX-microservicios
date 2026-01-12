package com.bca.core_banking_service.domain.services;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.transaction.Transfer;

public class TransferDomainService {

    public Transfer execute(Account source, Account destination, BigDecimal amount){

        source.withdraw(amount);
        destination.deposit(amount);

        return new Transfer();
    }
}
