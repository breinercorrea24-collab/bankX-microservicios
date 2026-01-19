package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;
import com.bca.core_banking_service.dto.CheckingAccountResponse;
import com.bca.core_banking_service.dto.FixedTermAccountResponse;
import com.bca.core_banking_service.dto.PymeCheckingAccountResponse;
import com.bca.core_banking_service.dto.SavingsAccountResponse;
import com.bca.core_banking_service.dto.VipSavingsAccountResponse;

class CustomerApiMapperTest {

    @Test
    @DisplayName("El constructor es privado y arroja IllegalStateException")
    void constructorIsPrivateAndThrowsUtilityException() throws Exception {
        Constructor<CustomerApiMapper> ctor = CustomerApiMapper.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertEquals("Utility class", thrown.getCause().getMessage());
    }

    @Test
    void toPolymorphicResponse_mapsSavingsAccountFields() {
        SavingsAccount acc = new SavingsAccount("cust-1", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS, 10, BigDecimal.ONE, BigDecimal.valueOf(123.45));
        acc.setId("sav-1");
        acc.setCurrentTransactions(2);

        SavingsAccountResponse resp = (SavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals("sav-1", resp.getId()),
            () -> assertEquals("cust-1", resp.getCustomerId()),
            () -> assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, resp.getType()),
            () -> assertEquals("USD", resp.getCurrency()),
            () -> assertEquals(123.45f, resp.getBalance()),
            () -> assertEquals(SavingsAccountResponse.StatusEnum.ACTIVE, resp.getStatus()),
            () -> assertEquals(10, resp.getMaxMonthlyTransactions()),
            () -> assertEquals(2, resp.getCurrentTransactions()),
            () -> assertEquals(BigDecimal.ONE, resp.getMaintenanceCommission())
        );
    }

    @Test
    void toPolymorphicResponse_appliesDefaultsForSavingsNulls() {
        SavingsAccount acc = new SavingsAccount("cust-2", "PEN", ProductStatus.BLOCKED, AccountType.SAVINGS, 0, BigDecimal.ONE, BigDecimal.TEN);
        acc.setId("sav-2");
        acc.setBalance(null);
        acc.setStatus(null);
        acc.setMaintenanceCommission(null);

        SavingsAccountResponse resp = (SavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals(0.0f, resp.getBalance()),
            () -> assertEquals(SavingsAccountResponse.StatusEnum.INACTIVE, resp.getStatus()),
            () -> assertEquals(BigDecimal.ZERO, resp.getMaintenanceCommission())
        );
    }

    @Test
    void toPolymorphicResponse_mapsVipSavingsAccountWithFallbacks() {
        VipSavingsAccount acc = new VipSavingsAccount("cust-3", "EUR", ProductStatus.ACTIVE, AccountType.VIP_SAVINGS, 7, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);
        acc.setId("vip-1");
        acc.setBalance(null);
        acc.setStatus(null);
        acc.setCurrentTransactions(3);
        acc.setMaintenanceCommission(null);
        acc.setMinimumDailyAverage(null);

        VipSavingsAccountResponse resp = (VipSavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals("vip-1", resp.getId()),
            () -> assertEquals("cust-3", resp.getCustomerId()),
            () -> assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, resp.getType()),
            () -> assertEquals("EUR", resp.getCurrency()),
            () -> assertEquals(0.0f, resp.getBalance()),
            () -> assertEquals(VipSavingsAccountResponse.StatusEnum.INACTIVE, resp.getStatus()),
            () -> assertEquals(7, resp.getMaxMonthlyTransactions()),
            () -> assertEquals(3, resp.getCurrentTransactions()),
            () -> assertEquals(BigDecimal.ZERO, resp.getMaintenanceCommission()),
            () -> assertEquals(BigDecimal.ZERO, resp.getMinimumDailyAverage())
        );
    }

    @Test
    void toPolymorphicResponse_mapsCheckingAccount() {
        CheckingAccount acc = new CheckingAccount("cust-4", "USD", ProductStatus.CLOSED, AccountType.CHECKING, 4, BigDecimal.valueOf(2.5), BigDecimal.TEN);
        acc.setId("chk-1");

        CheckingAccountResponse resp = (CheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals("chk-1", resp.getId()),
            () -> assertEquals("cust-4", resp.getCustomerId()),
            () -> assertEquals(com.bca.core_banking_service.dto.AccountType.CHECKING, resp.getType()),
            () -> assertEquals("USD", resp.getCurrency()),
            () -> assertEquals(10.0f, resp.getBalance()),
            () -> assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, resp.getStatus()),
            () -> assertEquals(4, resp.getMaxMonthlyTransactions()),
            () -> assertEquals(BigDecimal.valueOf(2.5), resp.getMaintenanceCommission())
        );
    }

    @Test
    void toPolymorphicResponse_mapsFixedTermAccount() {
        FixedTermAccount acc = new FixedTermAccount("cust-5", "GBP", ProductStatus.ACTIVE, AccountType.FIXED_TERM, BigDecimal.valueOf(3.25), true, 20, 1, BigDecimal.valueOf(50));
        acc.setId("ft-1");
        acc.setAllowedDay(15);

        FixedTermAccountResponse resp = (FixedTermAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals("ft-1", resp.getId()),
            () -> assertEquals("cust-5", resp.getCustomerId()),
            () -> assertEquals(com.bca.core_banking_service.dto.AccountType.FIXED_TERM, resp.getType()),
            () -> assertEquals("GBP", resp.getCurrency()),
            () -> assertEquals(50.0f, resp.getBalance()),
            () -> assertEquals(FixedTermAccountResponse.StatusEnum.ACTIVE, resp.getStatus()),
            () -> assertEquals(15, resp.getAllowedDay()),
            () -> assertEquals(BigDecimal.valueOf(3.25), resp.getInterestRate()),
            () -> assertTrue(resp.getMaintenanceFeeFree()),
            () -> assertEquals(20, resp.getAllowedMovementDay()),
            () -> assertEquals(1, resp.getMovementsThisMonth())
        );
    }

    @Test
    void toPolymorphicResponse_mapsPymeCheckingAccountWithDefaultStatus() {
        PymeCheckingAccount acc = new PymeCheckingAccount("cust-6", "USD", ProductStatus.ACTIVE, AccountType.PYME_CHECKING, 8, BigDecimal.valueOf(1.1), BigDecimal.valueOf(200));
        acc.setId("pyme-1");
        acc.setStatus(null);

        PymeCheckingAccountResponse resp = (PymeCheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertAll(
            () -> assertEquals("pyme-1", resp.getId()),
            () -> assertEquals("cust-6", resp.getCustomerId()),
            () -> assertEquals(com.bca.core_banking_service.dto.AccountType.PYME_CHECKING, resp.getType()),
            () -> assertEquals("USD", resp.getCurrency()),
            () -> assertEquals(200.0f, resp.getBalance()),
            () -> assertEquals(PymeCheckingAccountResponse.StatusEnum.INACTIVE, resp.getStatus()),
            () -> assertEquals(8, resp.getMaxMonthlyTransactions()),
            () -> assertEquals(BigDecimal.valueOf(1.1), resp.getMaintenanceCommission())
        );
    }

    @Test
    void toPolymorphicResponse_throwsForNullInputOrType() {
        IllegalArgumentException nullAccount = assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(null));
        assertEquals("Unsupported account type: null", nullAccount.getMessage());

        Account accountWithNullType = new SavingsAccount("c-null", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        accountWithNullType.setType(null);
        IllegalArgumentException nullType = assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(accountWithNullType));
        assertEquals("Unsupported account type: null", nullType.getMessage());
    }

    @Test
    @DisplayName("Métodos privados rechazan entradas nulas con mensajes claros")
    void privateMappersThrowForNulls() throws Exception {
        assertNullGuard("toVipSavingsResponse", VipSavingsAccount.class, "VipSavingsAccount cannot be null");
        assertNullGuard("toPymeCheckingAccountResponse", PymeCheckingAccount.class, "PymeCheckingAccount cannot be null");
        assertNullGuard("toSavingsResponse", SavingsAccount.class, "SavingsAccount cannot be null");
        assertNullGuard("toCheckingResponse", CheckingAccount.class, "CheckingAccount cannot be null");
        assertNullGuard("toFixedTermResponse", FixedTermAccount.class, "FixedTermAccount cannot be null");
    }

    private void assertNullGuard(String methodName, Class<?> paramType, String expectedMessage) throws Exception {
        Method method = CustomerApiMapper.class.getDeclaredMethod(methodName, paramType);
        method.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> method.invoke(null, new Object[]{null}));
        assertEquals(expectedMessage, thrown.getCause().getMessage());
    }
}
