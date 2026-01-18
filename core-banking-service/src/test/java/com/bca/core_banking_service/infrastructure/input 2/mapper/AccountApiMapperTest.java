package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.dto.TransactionResponse;

class AccountApiMapperTest {

    @Test
    void mapToAccountResponse_populatesAllFields() {
        SavingsAccount account = new SavingsAccount(
                "cus-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.valueOf(150));
        account.setId("acc-1");
        account.setStatus(ProductStatus.ACTIVE);

        AccountResponse response = AccountApiMapper.mapToAccountResponse(account);

        assertEquals("acc-1", response.getId());
        assertEquals("cus-1", response.getCustomerId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(150f, response.getBalance());
        assertEquals(AccountResponse.StatusEnum.ACTIVE, response.getStatus());
    }

    @Test
    void mapToTransactionResponse_buildsTransaction() {
        SavingsAccount account = new SavingsAccount(
                "cus-2",
                "EUR",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                4,
                BigDecimal.ZERO,
                BigDecimal.valueOf(80));
        account.setId("acc-2");

        TransactionResponse response = AccountApiMapper.mapToTransactionResponse(
                account, "DEPOSIT", BigDecimal.valueOf(20));

        assertEquals("acc-2", response.getAccountId());
        assertEquals(TransactionResponse.TypeEnum.DEPOSIT, response.getType());
        assertEquals(20f, response.getAmount());
        assertEquals(80f, response.getBalance());
        assertNotNull(response.getTransactionId());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void constructorIsPrivate() throws Exception {
        var constructor = AccountApiMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalStateException.class, thrown.getTargetException());
    }
}
