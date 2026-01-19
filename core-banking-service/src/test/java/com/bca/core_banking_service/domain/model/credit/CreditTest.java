package com.bca.core_banking_service.domain.model.credit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;

class CreditTest {

    private TestCredit credit;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        credit = new TestCredit(
                "CR-123",
                "CUST-001",
                CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000), // originalAmount
                BigDecimal.valueOf(1000), // pendingDebt
                BigDecimal.valueOf(15),   // interestRate %
                BigDecimal.valueOf(5000), // creditLimit
                12,                       // termMonths
                now,
                now.plusMonths(12),
                CreditStatus.ACTIVE);
    }

    @Test
    @DisplayName("makePayment reduce pendiente con monto válido")
    void makePayment_WithValidAmount_ReducesPendingDebt() {
        credit.makePayment(BigDecimal.valueOf(200));
        assertEquals(0, BigDecimal.valueOf(800).compareTo(credit.getPendingDebt()));
    }

    @Test
    @DisplayName("makePayment lanza cuando monto es cero/negativo")
    void makePayment_WithZeroOrNegativeAmount_ThrowsException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> credit.makePayment(BigDecimal.ZERO)),
                () -> assertThrows(IllegalArgumentException.class, () -> credit.makePayment(BigDecimal.valueOf(-10)))
        );
    }

    @Test
    @DisplayName("makePayment lanza cuando monto excede deuda")
    void makePayment_ExceedingDebt_ThrowsException() {
        BigDecimal tooMuch = BigDecimal.valueOf(1001);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> credit.makePayment(tooMuch));
        assertEquals("Payment exceeds outstanding balance", exception.getMessage());
    }

    @Test
    @DisplayName("getAvailableCredit devuelve límite - deuda")
    void getAvailableCredit_ReturnsCorrectDifference() {
        assertEquals(0, BigDecimal.valueOf(4000).compareTo(credit.getAvailableCredit()));

        credit.makePayment(BigDecimal.valueOf(500));
        assertEquals(0, BigDecimal.valueOf(4500).compareTo(credit.getAvailableCredit()));
    }

    @Test
    @DisplayName("Getters/Setters de Lombok funcionan")
    void verifyLombokDataAndGetters() {
        credit.setStatus(CreditStatus.PAID);
        credit.setInterestRate(BigDecimal.valueOf(20));

        assertAll(
                () -> assertEquals("CR-123", credit.getId()),
                () -> assertEquals(CreditStatus.PAID, credit.getStatus()),
                () -> assertEquals(BigDecimal.valueOf(20), credit.getInterestRate()),
                () -> assertEquals(BigDecimal.valueOf(5000), credit.getCreditLimit()),
                () -> assertEquals(CreditType.PERSONAL_LOAN, credit.getCreditType())
        );
    }

    private static class TestCredit extends Credit {
        TestCredit(String id, String customerId, CreditType creditType,
                   BigDecimal originalAmount, BigDecimal pendingDebt,
                   BigDecimal interestRate, BigDecimal creditLimit,
                   Integer termMonths, LocalDateTime createdAt,
                   LocalDateTime dueDate, CreditStatus status) {
            super(id, customerId, creditType, originalAmount, pendingDebt,
                    interestRate, creditLimit, termMonths, createdAt, dueDate, status);
        }
    }
}
