package com.bca.core_banking_service.domain.model.enums.account;

public enum AccountType {
    SAVINGS, CHECKING, FIXED_TERM, VIP_SAVINGS, PYME_CHECKING;

    private static final AccountType DEFAULT = SAVINGS;

    public static AccountType fromString(String value) {
        if (value == null) {
            return DEFAULT;
        }
        for (AccountType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
