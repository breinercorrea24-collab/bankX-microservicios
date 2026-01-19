package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
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
    void constructorIsPrivateAndThrowsUtilityException() throws Exception {
        Constructor<CustomerApiMapper> ctor = CustomerApiMapper.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", thrown.getCause().getMessage());
    }

    @Test
    void toPolymorphicResponse_mapsSavingsAccount() {
        SavingsAccount acc = new SavingsAccount("cust-1", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS,
                5, BigDecimal.ONE, BigDecimal.valueOf(100));
        acc.setId("s-1");
        acc.setCurrentTransactions(2);

        AccountPolymorphicResponse response = CustomerApiMapper.toPolymorphicResponse(acc);
        SavingsAccountResponse savings = (SavingsAccountResponse) response;

        assertEquals("s-1", savings.getId());
        assertEquals("cust-1", savings.getCustomerId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, savings.getType());
        assertEquals("USD", savings.getCurrency());
        assertEquals(100.0f, savings.getBalance());
        assertEquals(SavingsAccountResponse.StatusEnum.ACTIVE, savings.getStatus());
        assertEquals(5, savings.getMaxMonthlyTransactions());
        assertEquals(2, savings.getCurrentTransactions());
        assertEquals(BigDecimal.ONE, savings.getMaintenanceCommission());
    }

    @Test
    void toPolymorphicResponse_savingsDefaultsWhenNulls() {
        SavingsAccount acc = new SavingsAccount("cust-1", "USD", null, AccountType.SAVINGS,
                2, null, null);
        acc.setId("s-2");
        acc.setCurrentTransactions(0);

        SavingsAccountResponse savings = (SavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertEquals(0.0f, savings.getBalance());
        assertEquals(SavingsAccountResponse.StatusEnum.INACTIVE, savings.getStatus());
        assertEquals(BigDecimal.ZERO, savings.getMaintenanceCommission());
    }

    @Test
    void toPolymorphicResponse_mapsCheckingAccount() {
        CheckingAccount acc = new CheckingAccount("cust-2", "EUR", ProductStatus.ACTIVE, AccountType.CHECKING,
                3, BigDecimal.valueOf(2), BigDecimal.valueOf(50));
        acc.setId("c-1");

        AccountPolymorphicResponse response = CustomerApiMapper.toPolymorphicResponse(acc);
        CheckingAccountResponse checking = (CheckingAccountResponse) response;

        assertEquals("c-1", checking.getId());
        assertEquals("cust-2", checking.getCustomerId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.CHECKING, checking.getType());
        assertEquals("EUR", checking.getCurrency());
        assertEquals(50.0f, checking.getBalance());
        assertEquals(3, checking.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.valueOf(2), checking.getMaintenanceCommission());
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, checking.getStatus());
    }

    @Test
    void toPolymorphicResponse_mapsCheckingAccountWithNullsDefaultsBalanceAndStatus() {
        CheckingAccount acc = new CheckingAccount("cust-2", "EUR", null, AccountType.CHECKING,
                3, BigDecimal.valueOf(2), null);
        acc.setId("c-2");

        CheckingAccountResponse checking = (CheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertEquals(0.0f, checking.getBalance());
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, checking.getStatus()); // hardcoded ACTIVE
    }

    @Test
    void toPolymorphicResponse_mapsFixedTermAccount() {
        FixedTermAccount acc = new FixedTermAccount("cust-3", "USD", ProductStatus.ACTIVE, AccountType.FIXED_TERM,
                BigDecimal.valueOf(4.5), true, 20, 1, BigDecimal.valueOf(500));
        acc.setId("f-1");
        acc.setAllowedDay(15);

        AccountPolymorphicResponse response = CustomerApiMapper.toPolymorphicResponse(acc);
        FixedTermAccountResponse fixed = (FixedTermAccountResponse) response;

        assertEquals("f-1", fixed.getId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.FIXED_TERM, fixed.getType());
        assertEquals("USD", fixed.getCurrency());
        assertEquals(500.0f, fixed.getBalance());
        assertEquals(15, fixed.getAllowedDay());
        assertEquals(BigDecimal.valueOf(4.5), fixed.getInterestRate());
        assertTrue(fixed.getMaintenanceFeeFree());
        assertEquals(20, fixed.getAllowedMovementDay());
        assertEquals(1, fixed.getMovementsThisMonth());
        assertEquals(FixedTermAccountResponse.StatusEnum.ACTIVE, fixed.getStatus());
    }

    @Test
    void toPolymorphicResponse_fixedTermDefaultsWhenNulls() {
        FixedTermAccount acc = new FixedTermAccount("cust-3", "USD", null, AccountType.FIXED_TERM,
                null, false, null, null, null);
        acc.setId("f-2");

        FixedTermAccountResponse fixed = (FixedTermAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertEquals(0.0f, fixed.getBalance());
        assertEquals(FixedTermAccountResponse.StatusEnum.ACTIVE, fixed.getStatus()); // hardcoded ACTIVE
    }

    @Test
    void toPolymorphicResponse_mapsVipSavingsAccount() {
        VipSavingsAccount acc = new VipSavingsAccount("cust-4", "USD", ProductStatus.ACTIVE, AccountType.VIP_SAVINGS,
                8, BigDecimal.valueOf(1), BigDecimal.valueOf(250), BigDecimal.valueOf(1000));
        acc.setId("vip-1");
        acc.setCurrentTransactions(3);

        AccountPolymorphicResponse response = CustomerApiMapper.toPolymorphicResponse(acc);
        VipSavingsAccountResponse vip = (VipSavingsAccountResponse) response;

        assertEquals("vip-1", vip.getId());
        // Mapper sets type to SAVINGS for VIP accounts
        assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, vip.getType());
        assertEquals("USD", vip.getCurrency());
        assertEquals(250.0f, vip.getBalance());
        assertEquals(VipSavingsAccountResponse.StatusEnum.ACTIVE, vip.getStatus());
        assertEquals(8, vip.getMaxMonthlyTransactions());
        assertEquals(3, vip.getCurrentTransactions());
        assertEquals(BigDecimal.valueOf(1), vip.getMaintenanceCommission());
        assertEquals(BigDecimal.valueOf(1000), vip.getMinimumDailyAverage());
    }

    @Test
    void toPolymorphicResponse_vipDefaultsWhenNulls() {
        VipSavingsAccount acc = new VipSavingsAccount("cust-4", "USD", null, AccountType.VIP_SAVINGS,
                0, null, null, null);
        acc.setId("vip-2");

        VipSavingsAccountResponse vip = (VipSavingsAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertEquals(0.0f, vip.getBalance());
        assertEquals(VipSavingsAccountResponse.StatusEnum.INACTIVE, vip.getStatus());
        assertEquals(BigDecimal.ZERO, vip.getMaintenanceCommission());
        assertEquals(BigDecimal.ZERO, vip.getMinimumDailyAverage());
    }

    @Test
    void toPolymorphicResponse_mapsPymeCheckingAccount() {
        PymeCheckingAccount acc = new PymeCheckingAccount("cust-5", "USD", ProductStatus.ACTIVE,
                AccountType.PYME_CHECKING, 6, BigDecimal.valueOf(3), BigDecimal.valueOf(200));
        acc.setId("p-1");

        AccountPolymorphicResponse response = CustomerApiMapper.toPolymorphicResponse(acc);
        PymeCheckingAccountResponse pyme = (PymeCheckingAccountResponse) response;

        assertEquals("p-1", pyme.getId());
        assertEquals(com.bca.core_banking_service.dto.AccountType.PYME_CHECKING, pyme.getType());
        assertEquals("USD", pyme.getCurrency());
        assertEquals(200.0f, pyme.getBalance());
        assertEquals(PymeCheckingAccountResponse.StatusEnum.ACTIVE, pyme.getStatus());
        assertEquals(6, pyme.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.valueOf(3), pyme.getMaintenanceCommission());
    }

    @Test
    void toPolymorphicResponse_pymeDefaultsWhenNulls() {
        PymeCheckingAccount acc = new PymeCheckingAccount("cust-5", "USD", null,
                AccountType.PYME_CHECKING, 0, null, null);
        acc.setId("p-2");

        PymeCheckingAccountResponse pyme = (PymeCheckingAccountResponse) CustomerApiMapper.toPolymorphicResponse(acc);

        assertEquals(0.0f, pyme.getBalance());
        assertEquals(PymeCheckingAccountResponse.StatusEnum.INACTIVE, pyme.getStatus());
    }

    @Test
    void toPolymorphicResponse_throwsForNullAccount() {
        assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(null));
    }

    @Test
    void toPolymorphicResponse_throwsForNullType() {
        SavingsAccount acc = new SavingsAccount("cust-6", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS,
                1, BigDecimal.ZERO, BigDecimal.ZERO);
        acc.setType(null);

        assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(acc));
    }

    @Test
    void toPolymorphicResponse_throwsForUnsupportedTypeEvenIfNonNull() {
        // Create anonymous subclass overriding getType to simulate unsupported path
        Account unsupported = new SavingsAccount("cust-x", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS,
                1, BigDecimal.ONE, BigDecimal.ONE) {
            @Override
            public com.bca.core_banking_service.domain.model.enums.account.AccountType getType() {
                return null; // bypass enum switch to hit null guard
            }
        };
        assertThrows(IllegalArgumentException.class, () -> CustomerApiMapper.toPolymorphicResponse(unsupported));
    }

    @Test
    void toPolymorphicResponse_whenTypeDoesNotMatchInstance_throwsClassCast() {
        // Mismatch: type says CHECKING but object is SavingsAccount; switch enters CHECKING and cast blows up
        Account mismatched = new SavingsAccount("cust-y", "USD", ProductStatus.ACTIVE, AccountType.SAVINGS,
                1, BigDecimal.ONE, BigDecimal.ONE);
        mismatched.setType(AccountType.CHECKING);

        assertThrows(ClassCastException.class, () -> CustomerApiMapper.toPolymorphicResponse(mismatched));
    }

    @Test
    void helperMethods_throwWhenNullInput() throws Exception {
        assertIllegalArgumentForHelper("toVipSavingsResponse", VipSavingsAccount.class);
        assertIllegalArgumentForHelper("toPymeCheckingAccountResponse", PymeCheckingAccount.class);
        assertIllegalArgumentForHelper("toSavingsResponse", SavingsAccount.class);
        assertIllegalArgumentForHelper("toCheckingResponse", CheckingAccount.class);
        assertIllegalArgumentForHelper("toFixedTermResponse", FixedTermAccount.class);
    }

    private void assertIllegalArgumentForHelper(String methodName, Class<?> paramType) throws Exception {
        Method method = CustomerApiMapper.class.getDeclaredMethod(methodName, paramType);
        method.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> method.invoke(null, new Object[] { null }));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }
}
