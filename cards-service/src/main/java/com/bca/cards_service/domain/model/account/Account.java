package com.bca.cards_service.domain.model.account;

import java.math.BigDecimal;


import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Account {

    protected String accountNumber;
    protected BigDecimal balance;
    protected String currency;


    public enum AccountStatus {
        ACTIVE, INACTIVE
    }

}
