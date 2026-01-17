package com.bca.core_banking_service.infrastructure.input.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.bca.core_banking_service.dto.CreditPaymentResponse;
import com.bca.core_banking_service.dto.CreditResponse;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;

public class CreditApiMapper {
    private CreditApiMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static CreditResponse mapToCreditResponse(Credit credit) {
        CreditResponse response = new CreditResponse();
        response.setId(credit.getId());
        response.setCustomerId(credit.getCustomerId());
        response.setCreditType(CreditResponse.CreditTypeEnum.valueOf(credit.getCreditType().name()));
        response.setOriginalAmount(credit.getOriginalAmount().doubleValue());
        response.setPendingDebt(credit.getPendingDebt().doubleValue());
        response.setInterestRate(credit.getInterestRate().doubleValue());
        response.setTermMonths(credit.getTermMonths());
        response.setStatus(credit.getStatus().name());
        response.setCreatedAt(OffsetDateTime.ofInstant(credit.getCreatedAt().toInstant(java.time.ZoneOffset.UTC),
                java.time.ZoneOffset.UTC));
        return response;
    }

    public static CreditPaymentResponse mapToCreditPaymentResponse(Credit credit, BigDecimal paidAmount) {
        CreditPaymentResponse response = new CreditPaymentResponse();
        response.setCreditId(credit.getId());
        response.setPaymentId("pay-" + System.currentTimeMillis());
        response.setPaidAmount(paidAmount.doubleValue());
        response.setRemainingDebt(credit.getPendingDebt().doubleValue());
        response.setStatus(
                credit.getPendingDebt().compareTo(BigDecimal.ZERO) == 0 ? CreditPaymentResponse.StatusEnum.FULL_PAYMENT
                        : CreditPaymentResponse.StatusEnum.PARTIAL_PAYMENT);
        response.setPaidAt(OffsetDateTime.now());
        return response;
    }
}
