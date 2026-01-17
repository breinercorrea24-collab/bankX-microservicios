package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;
import com.bca.core_banking_service.dto.AccountPolymorphicResponse;
import com.bca.core_banking_service.dto.CheckingAccountResponse;
import com.bca.core_banking_service.dto.FixedTermAccountResponse;
import com.bca.core_banking_service.dto.PymeCheckingAccountResponse;
import com.bca.core_banking_service.dto.SavingsAccountResponse;
import com.bca.core_banking_service.dto.VipSavingsAccountResponse;

class CustomerApiMapperTest {

    @Test
    void toPolymorphicResponse_mapsSavingsAccount() {
        SavingsAccount account = new SavingsAccount(
                "cus-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ONE,
                BigDecimal.valueOf(100));
        account.setId("acc-1");
        account.setCurrentTransactions(2);
        account.setStatus(null); // triggers default enum mapping to INACTIVE

        AccountPolymorphicResponse result = CustomerApiMapper.toPolymorphicResponse(account);

        SavingsAccountResponse response = assertInstanceOf(SavingsAccountResponse.class, result);
        assertEquals("acc-1", response.getId());
        assertEquals("cus-1", response.getCustomerId());
        assertEquals(SavingsAccountResponse.StatusEnum.INACTIVE, response.getStatus());
        assertEquals(2, response.getCurrentTransactions());
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, response.getType());
    }

    @Test
    void savingsResponseDefaultsOptionalFields() {
        SavingsAccount account = new SavingsAccount(
                "cus-def",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                7,
                BigDecimal.ONE,
                BigDecimal.valueOf(80));
        account.setId("acc-defaults");
        account.setBalance(null);
        account.setMaintenanceCommission(null);

        SavingsAccountResponse response = (SavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(account);

        assertEquals("acc-defaults", response.getId());
        assertEquals(0f, response.getBalance());
        assertEquals(BigDecimal.ZERO, response.getMaintenanceCommission());
    }

    @Test
    void toPolymorphicResponse_mapsCheckingAccount() {
        CheckingAccount account = new CheckingAccount(
                "cus-2",
                "PEN",
                ProductStatus.BLOCKED,
                AccountType.CHECKING,
                10,
                BigDecimal.TEN,
                BigDecimal.valueOf(50));
        account.setId("chk-1");

        AccountPolymorphicResponse result = CustomerApiMapper.toPolymorphicResponse(account);

        CheckingAccountResponse response = assertInstanceOf(CheckingAccountResponse.class, result);
        assertEquals("chk-1", response.getId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.CHECKING, response.getType());
        assertEquals(BigDecimal.TEN, response.getMaintenanceCommission());
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, response.getStatus());
    }

    @Test
    void checkingResponseDefaultsMissingBalance() {
        CheckingAccount account = new CheckingAccount(
                "cus-ck",
                "PEN",
                ProductStatus.ACTIVE,
                AccountType.CHECKING,
                3,
                BigDecimal.ONE,
                BigDecimal.valueOf(40));
        account.setId("chk-null");
        account.setBalance(null);

        CheckingAccountResponse response = (CheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(account);

        assertEquals("chk-null", response.getId());
        assertEquals(0f, response.getBalance());
    }

    @Test
    void toPolymorphicResponse_mapsVipSavingsAccount() {
        VipSavingsAccount account = new VipSavingsAccount(
                "cus-3",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.VIP_SAVINGS,
                3,
                BigDecimal.ZERO,
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(200));
        account.setId("vip-1");
        account.setStatus(null); // ensures mapper falls back to INACTIVE
        account.setCurrentTransactions(1);

        AccountPolymorphicResponse result = CustomerApiMapper.toPolymorphicResponse(account);

        VipSavingsAccountResponse response = assertInstanceOf(VipSavingsAccountResponse.class, result);
        assertEquals("vip-1", response.getId());
        assertEquals(VipSavingsAccountResponse.StatusEnum.INACTIVE, response.getStatus());
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, response.getType()); // mapper hard-codes SAVINGS
        assertEquals(BigDecimal.valueOf(200), response.getMinimumDailyAverage());
        assertEquals(30f, response.getBalance());
    }

    @Test
    void vipSavingsResponseDefaultsMissingFields() {
        VipSavingsAccount account = new VipSavingsAccount(
                "cus-3",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.VIP_SAVINGS,
                3,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
        account.setId("vip-defaults");
        account.setBalance(null);
        account.setMaintenanceCommission(null);
        account.setMinimumDailyAverage(null);

        VipSavingsAccountResponse response = (VipSavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(
                account);

        assertEquals("vip-defaults", response.getId());
        assertEquals(0f, response.getBalance());
        assertEquals(BigDecimal.ZERO, response.getMaintenanceCommission());
        assertEquals(BigDecimal.ZERO, response.getMinimumDailyAverage());
        assertEquals(VipSavingsAccountResponse.StatusEnum.ACTIVE, response.getStatus());
    }

    @Test
    void toPolymorphicResponse_mapsPymeCheckingAccount() {
        PymeCheckingAccount account = new PymeCheckingAccount(
                "cus-4",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.PYME_CHECKING,
                20,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(300));
        account.setId("pyme-1");

        AccountPolymorphicResponse result = CustomerApiMapper.toPolymorphicResponse(account);

        PymeCheckingAccountResponse response = assertInstanceOf(PymeCheckingAccountResponse.class, result);
        assertEquals("pyme-1", response.getId());
        assertEquals(PymeCheckingAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(com.bca.core_banking_service.dto.AccountType.PYME_CHECKING, response.getType());
        assertEquals(BigDecimal.valueOf(5), response.getMaintenanceCommission());
    }

    @Test
    void pymeCheckingResponseDefaultsBalance() {
        PymeCheckingAccount account = new PymeCheckingAccount(
                "cus-4",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.PYME_CHECKING,
                8,
                BigDecimal.ONE,
                BigDecimal.valueOf(150));
        account.setId("pyme-null-balance");
        account.setBalance(null);

        PymeCheckingAccountResponse response = (PymeCheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(
                account);

        assertEquals("pyme-null-balance", response.getId());
        assertEquals(0f, response.getBalance());
    }

    @Test
    void pymeCheckingAccountDefaultsStatusWhenNull() {
        PymeCheckingAccount account = new PymeCheckingAccount(
                "cus-6",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.PYME_CHECKING,
                10,
                BigDecimal.ONE,
                BigDecimal.TEN);
        account.setId("pyme-2");
        account.setStatus(null);

        PymeCheckingAccountResponse response = (PymeCheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(
                account);
        assertEquals(PymeCheckingAccountResponse.StatusEnum.INACTIVE, response.getStatus());
    }

    @Test
    void toPolymorphicResponse_mapsFixedTermAccount() {
        FixedTermAccount account = new FixedTermAccount(
                "cus-5",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.FIXED_TERM,
                BigDecimal.valueOf(0.05),
                true,
                25,
                1,
                BigDecimal.valueOf(500));
        account.setId("ft-1");
        account.setAllowedDay(15);

        AccountPolymorphicResponse result = CustomerApiMapper.toPolymorphicResponse(account);

        FixedTermAccountResponse response = assertInstanceOf(FixedTermAccountResponse.class, result);
        assertEquals("ft-1", response.getId());
        assertEquals(15, response.getAllowedDay());
        assertEquals(Integer.valueOf(25), response.getAllowedMovementDay());
        assertEquals(Integer.valueOf(1), response.getMovementsThisMonth());
        assertEquals(Boolean.TRUE, response.getMaintenanceFeeFree());
    }

    @Test
    void fixedTermResponseDefaultsBalance() {
        FixedTermAccount account = new FixedTermAccount(
                "cus-5",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.FIXED_TERM,
                BigDecimal.valueOf(0.08),
                false,
                12,
                0,
                BigDecimal.valueOf(400));
        account.setId("ft-defaults");
        account.setBalance(null);

        FixedTermAccountResponse response = (FixedTermAccountResponse) CustomerApiMapper.toPolymorphicResponse(account);

        assertEquals("ft-defaults", response.getId());
        assertEquals(0f, response.getBalance());
    }

    @Test
    void mapperThrowsWhenSavingsAccountNull() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrivateMapper("toSavingsResponse", SavingsAccount.class, null));
    }

    @Test
    void mapperThrowsWhenVipSavingsAccountNull() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrivateMapper("toVipSavingsResponse", VipSavingsAccount.class, null));
    }

    @Test
    void mapperThrowsWhenCheckingAccountNull() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrivateMapper("toCheckingResponse", CheckingAccount.class, null));
    }

    @Test
    void mapperThrowsWhenFixedTermAccountNull() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrivateMapper("toFixedTermResponse", FixedTermAccount.class, null));
    }

    @Test
    void mapperThrowsWhenPymeCheckingAccountNull() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrivateMapper("toPymeCheckingAccountResponse", PymeCheckingAccount.class, null));
    }

    @Test
    void mapperThrowsWhenAccountNull() {
        assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(null));
    }

    @Test
    void mapperThrowsWhenAccountTypeNull() {
        SavingsAccount account = new SavingsAccount(
                "cus-null",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.TEN);
        account.setType(null);

        assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(account));
    }

    @Test
    void constructorIsPrivate() throws Exception {
        var constructor = CustomerApiMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalStateException.class, thrown.getTargetException());
    }

    private static Object invokePrivateMapper(String methodName, Class<?> parameterType, Object argument)
            throws Throwable {
        Method method = CustomerApiMapper.class.getDeclaredMethod(methodName, parameterType);
        method.setAccessible(true);
        try {
            return method.invoke(null, argument);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
