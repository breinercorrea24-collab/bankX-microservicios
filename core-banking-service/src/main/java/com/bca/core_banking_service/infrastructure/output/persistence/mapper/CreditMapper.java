package com.bca.core_banking_service.infrastructure.output.persistence.mapper;

import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.CreditEntity;

public class CreditMapper {

    private CreditMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Credit toDomain(CreditEntity e) {
        return new Credit(
            e.getId(),
            e.getCustomerId(),
            CreditType.valueOf(e.getCreditType().name()),
            e.getOriginalAmount(),
            e.getPendingDebt(),
            e.getInterestRate(),
            e.getTermMonths(),
            CreditStatus.valueOf(e.getStatus().name()),
            e.getCreatedAt(),
            e.getDueDate()
        );
    }

    public static CreditEntity toEntity(Credit d) {
        CreditEntity e = new CreditEntity();
        e.setId(d.getId());
        e.setCustomerId(d.getCustomerId());
        e.setCreditType(CreditEntity.CreditType.valueOf(d.getCreditType().name()));
        e.setOriginalAmount(d.getOriginalAmount());
        e.setPendingDebt(d.getPendingDebt());
        e.setInterestRate(d.getInterestRate());
        e.setTermMonths(d.getTermMonths());
        e.setStatus(CreditEntity.CreditStatus.valueOf(d.getStatus().name()));
        e.setCreatedAt(d.getCreatedAt());
        e.setDueDate(d.getDueDate());
        return e;
    }
}
