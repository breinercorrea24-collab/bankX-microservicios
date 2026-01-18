package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

/**
 * Sanity tests that exercise the OpenAPI generated DTOs so they count toward coverage.
 * Each test instantiates the DTO, uses the fluent setters, and verifies the getters/enums.
 */
class DtoCoverageTest {

    @Test
    void accountCreate_buildsAndReadsFields() {
        AccountCreate dto = new AccountCreate()
                .customerId("cus-1")
                .type(AccountCreate.TypeEnum.SAVINGS)
                .currency("USD");

        assertEquals("cus-1", dto.getCustomerId());
        assertEquals(AccountCreate.TypeEnum.SAVINGS, dto.getType());
        assertEquals("USD", dto.getCurrency());

        assertEquals(AccountCreate.TypeEnum.CHECKING, AccountCreate.TypeEnum.fromValue("CHECKING"));
        assertThrows(IllegalArgumentException.class, () -> AccountCreate.TypeEnum.fromValue("UNKNOWN"));
    }

    @Test
    void accountResponse_supportsStatusEnum() {
        AccountResponse response = new AccountResponse()
                .id("acc-1")
                .customerId("cus-1")
                .type(AccountType.SAVINGS)
                .currency("EUR")
                .balance(100f)
                .status(AccountResponse.StatusEnum.ACTIVE);

        assertEquals("acc-1", response.getId());
        assertEquals(AccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(AccountType.SAVINGS, response.getType());

        assertEquals(AccountResponse.StatusEnum.INACTIVE, AccountResponse.StatusEnum.fromValue("INACTIVE"));
        assertThrows(IllegalArgumentException.class, () -> AccountResponse.StatusEnum.fromValue("BAD"));
    }

    @Test
    void savingsAccountResponse_mapsMaintenanceCommission() {
        SavingsAccountResponse dto = new SavingsAccountResponse()
                .id("s-1")
                .customerId("cus")
                .type(AccountType.SAVINGS)
                .currency("USD")
                .balance(20f)
                .status(SavingsAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(5)
                .currentTransactions(1)
                .maintenanceCommission(BigDecimal.ONE);

        assertEquals("s-1", dto.getId());
        assertEquals(1, dto.getCurrentTransactions());
        assertEquals(BigDecimal.ONE, dto.getMaintenanceCommission());

        assertEquals(SavingsAccountResponse.StatusEnum.ACTIVE,
                SavingsAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> SavingsAccountResponse.StatusEnum.fromValue("NONE"));
    }

    @Test
    void vipSavingsAccountResponse_tracksMinimumAverage() {
        VipSavingsAccountResponse dto = new VipSavingsAccountResponse()
                .id("vip-1")
                .customerId("cus")
                .type(AccountType.VIP_SAVINGS)
                .currency("USD")
                .balance(200f)
                .status(VipSavingsAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(10)
                .currentTransactions(2)
                .maintenanceCommission(BigDecimal.TEN)
                .minimumDailyAverage(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), dto.getMinimumDailyAverage());
        assertEquals(VipSavingsAccountResponse.StatusEnum.ACTIVE, dto.getStatus());
    }

    @Test
    void fixedTermAccountResponse_mapsAllowedFields() {
        FixedTermAccountResponse dto = new FixedTermAccountResponse()
                .id("ft-1")
                .customerId("cus")
                .type(AccountType.FIXED_TERM)
                .currency("USD")
                .balance(1000f)
                .status(FixedTermAccountResponse.StatusEnum.INACTIVE)
                .allowedDay(15)
                .interestRate(BigDecimal.valueOf(3.5))
                .maintenanceFeeFree(Boolean.TRUE)
                .allowedMovementDay(20)
                .movementsThisMonth(2);

        assertEquals(15, dto.getAllowedDay());
        assertEquals(BigDecimal.valueOf(3.5), dto.getInterestRate());
        assertEquals(Boolean.TRUE, dto.getMaintenanceFeeFree());
        assertEquals(2, dto.getMovementsThisMonth());
    }

    @Test
    void checkingAccountResponse_mapsCommission() {
        CheckingAccountResponse dto = new CheckingAccountResponse()
                .id("chk-1")
                .customerId("cus")
                .type(AccountType.CHECKING)
                .currency("USD")
                .balance(50f)
                .status(CheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(30)
                .maintenanceCommission(BigDecimal.ZERO);

        assertEquals("chk-1", dto.getId());
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, dto.getStatus());
        assertEquals(BigDecimal.ZERO, dto.getMaintenanceCommission());
    }

    @Test
    void pymeCheckingAccountResponse_mapsTypeAndStatus() {
        PymeCheckingAccountResponse dto = new PymeCheckingAccountResponse()
                .id("pyme-1")
                .customerId("cus")
                .type(AccountType.PYME_CHECKING)
                .currency("USD")
                .balance(500f)
                .status(PymeCheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(40)
                .maintenanceCommission(BigDecimal.valueOf(12));

        assertEquals(AccountType.PYME_CHECKING, dto.getType());
        assertEquals(PymeCheckingAccountResponse.StatusEnum.ACTIVE, dto.getStatus());
        assertEquals(BigDecimal.valueOf(12), dto.getMaintenanceCommission());
    }

    @Test
    void creditResponse_populatesAllFields() {
        OffsetDateTime now = OffsetDateTime.now();
        CreditResponse dto = new CreditResponse()
                .id("cr-1")
                .customerId("cus")
                .creditType(CreditResponse.CreditTypeEnum.AUTO_LOAN)
                .originalAmount(10000d)
                .pendingDebt(9000d)
                .interestRate(15d)
                .termMonths(12)
                .status("APPROVED")
                .createdAt(now);

        assertEquals("cr-1", dto.getId());
        assertEquals(9000d, dto.getPendingDebt());
        assertEquals(now, dto.getCreatedAt());

        assertEquals(CreditResponse.CreditTypeEnum.MORTGAGE,
                CreditResponse.CreditTypeEnum.fromValue("MORTGAGE"));
        assertThrows(IllegalArgumentException.class,
                () -> CreditResponse.CreditTypeEnum.fromValue("XYZ"));
    }

    @Test
    void creditPaymentResponse_supportsEnumsAndDates() {
        OffsetDateTime paidAt = OffsetDateTime.now();
        CreditPaymentResponse dto = new CreditPaymentResponse()
                .creditId("cr-1")
                .paymentId("pm-1")
                .paidAmount(1000d)
                .remainingDebt(8000d)
                .status(CreditPaymentResponse.StatusEnum.PARTIAL_PAYMENT)
                .paidAt(paidAt);

        assertEquals("pm-1", dto.getPaymentId());
        assertEquals(CreditPaymentResponse.StatusEnum.PARTIAL_PAYMENT, dto.getStatus());
        assertEquals(paidAt, dto.getPaidAt());

        assertEquals(CreditPaymentResponse.StatusEnum.FULL_PAYMENT,
                CreditPaymentResponse.StatusEnum.fromValue("FULL_PAYMENT"));
        assertThrows(IllegalArgumentException.class,
                () -> CreditPaymentResponse.StatusEnum.fromValue("OTHER"));
    }

    @Test
    void creditCreate_handlesRequiredFields() {
        CreditCreate dto = new CreditCreate()
                .customerId("cus")
                .creditType(CreditCreate.CreditTypeEnum.PERSONAL_LOAN)
                .amount(2500f)
                .termMonths(24)
                .interestRate(10f);

        assertEquals("cus", dto.getCustomerId());
        assertEquals(24, dto.getTermMonths());
        assertEquals(10f, dto.getInterestRate());
    }

    @Test
    void amountRequest_andTransferRequest_coverSimpleDtos() {
        AmountRequest amountRequest = new AmountRequest().amount(99f);
        assertEquals(99f, amountRequest.getAmount());

        TransferRequest transferRequest = new TransferRequest()
                .fromId("from")
                .toId("to")
                .amount(120f);

        assertEquals("from", transferRequest.getFromId());
        assertEquals("to", transferRequest.getToId());
        assertEquals(120f, transferRequest.getAmount());
    }

    @Test
    void transactionResponse_mapsTypeEnum() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        TransactionResponse dto = new TransactionResponse()
                .transactionId("tx-1")
                .accountId("acc-1")
                .fromAccountId("acc-1")
                .toAccountId("acc-2")
                .type(TransactionResponse.TypeEnum.TRANSFER)
                .amount(50f)
                .balance(100f)
                .timestamp(timestamp);

        assertEquals("tx-1", dto.getTransactionId());
        assertEquals(TransactionResponse.TypeEnum.TRANSFER, dto.getType());
        assertEquals(timestamp, dto.getTimestamp());

        assertEquals(TransactionResponse.TypeEnum.DEPOSIT,
                TransactionResponse.TypeEnum.fromValue("DEPOSIT"));
        assertThrows(IllegalArgumentException.class,
                () -> TransactionResponse.TypeEnum.fromValue("UNKNOWN"));
    }

    @Test
    void errorResponse_tracksAllFields() {
        OffsetDateTime now = OffsetDateTime.now();
        ErrorResponse dto = new ErrorResponse()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("missing data")
                .path("/api/accounts");

        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("/api/accounts", dto.getPath());
        assertEquals(now, dto.getTimestamp());
    }

    @Test
    void accountType_enumFromValueValidatesInput() {
        assertEquals(AccountType.SAVINGS, AccountType.fromValue("SAVINGS"));
        assertThrows(IllegalArgumentException.class, () -> AccountType.fromValue("invalid"));
        assertEquals("SAVINGS", AccountType.SAVINGS.getValue());
        assertEquals("SAVINGS", AccountType.SAVINGS.toString());
    }
}
