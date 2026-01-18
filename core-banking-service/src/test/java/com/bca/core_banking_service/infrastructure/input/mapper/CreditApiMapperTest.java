package com.bca.core_banking_service.infrastructure.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.dto.CreditPaymentResponse;
import com.bca.core_banking_service.dto.CreditResponse;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditType;

class CreditApiMapperTest {

    @Test
    void mapToCreditResponse_convertsBasicFields() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 10, 14, 30);
        Credit credit = new Credit(
                "cr-1",
                "customer-1",
                CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(7500),
                BigDecimal.valueOf(10),
                24,
                CreditStatus.ACTIVE,
                createdAt, LocalDateTime.now().plusMonths(24));

        CreditResponse response = CreditApiMapper.mapToCreditResponse(credit);

        assertEquals("cr-1", response.getId());
        assertEquals("customer-1", response.getCustomerId());
        assertEquals(CreditResponse.CreditTypeEnum.PERSONAL_LOAN, response.getCreditType());
        assertEquals(10000d, response.getOriginalAmount());
        assertEquals(7500d, response.getPendingDebt());
        assertEquals(10d, response.getInterestRate());
        assertEquals(24, response.getTermMonths());
        assertEquals(CreditStatus.ACTIVE.name(), response.getStatus());
        OffsetDateTime expectedCreated = createdAt.atOffset(java.time.ZoneOffset.UTC);
        assertEquals(expectedCreated, response.getCreatedAt());
    }

    @Test
    void mapToCreditPaymentResponse_marksFullAndPartialPayments() {
        LocalDateTime createdAt = LocalDateTime.now();
        Credit credit = new Credit(
                "cr-2",
                "customer-2",
                CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(8),
                12,
                CreditStatus.ACTIVE,
                createdAt, LocalDateTime.now().plusMonths(12));

        CreditPaymentResponse partialResponse = CreditApiMapper.mapToCreditPaymentResponse(
                credit, BigDecimal.valueOf(300));

        assertEquals("cr-2", partialResponse.getCreditId());
        assertEquals(300d, partialResponse.getPaidAmount());
        assertEquals(1200d, partialResponse.getRemainingDebt());
        assertEquals(CreditPaymentResponse.StatusEnum.PARTIAL_PAYMENT, partialResponse.getStatus());
        assertTrue(partialResponse.getPaymentId().startsWith("pay-"));
        assertNotNull(partialResponse.getPaidAt());

        credit.setPendingDebt(BigDecimal.ZERO);
        CreditPaymentResponse fullResponse = CreditApiMapper.mapToCreditPaymentResponse(
                credit, BigDecimal.valueOf(1200));
        assertEquals(CreditPaymentResponse.StatusEnum.FULL_PAYMENT, fullResponse.getStatus());
    }

    @Test
    void constructorIsPrivate() throws Exception {
        var constructor = CreditApiMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalStateException.class, thrown.getTargetException());
    }
}
