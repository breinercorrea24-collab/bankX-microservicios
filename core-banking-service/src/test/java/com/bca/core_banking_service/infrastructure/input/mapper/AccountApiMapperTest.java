package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.dto.TransactionResponse;

class AccountApiMapperTest {

    @Test
    void constructorIsPrivateAndThrowsUtilityException() throws Exception {
        Constructor<AccountApiMapper> ctor = AccountApiMapper.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertTrue(thrown.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", thrown.getCause().getMessage());
    }

    @Test
    void mapToAccountResponse_mapsAllFields() {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                3,
                BigDecimal.ONE,
                BigDecimal.valueOf(150));
        account.setId("acc-1");
        account.setType(AccountType.SAVINGS);

        AccountResponse response = AccountApiMapper.mapToAccountResponse(account);

        assertEquals("acc-1", response.getId());
        assertEquals("customer-1", response.getCustomerId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(150.0f, response.getBalance());
        assertEquals(AccountResponse.StatusEnum.ACTIVE, response.getStatus());
    }

    @Test
    void mapToTransactionResponse_mapsTransactionFields() {
        SavingsAccount account = new SavingsAccount(
                "customer-2",
                "EUR",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                2,
                BigDecimal.ZERO,
                BigDecimal.valueOf(500));
        account.setId("acc-2");

        TransactionResponse response = AccountApiMapper.mapToTransactionResponse(
                account,
                "DEPOSIT",
                BigDecimal.valueOf(75));

        assertNotNull(response.getTransactionId());
        assertEquals("acc-2", response.getAccountId());
        assertEquals(TransactionResponse.TypeEnum.DEPOSIT, response.getType());
        assertEquals(75.0f, response.getAmount());
        assertEquals(500.0f, response.getBalance());
        assertNotNull(response.getTimestamp());
    }
}
