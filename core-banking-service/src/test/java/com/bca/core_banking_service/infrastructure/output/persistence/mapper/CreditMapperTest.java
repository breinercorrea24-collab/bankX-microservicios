package com.bca.core_banking_service.infrastructure.output.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditStatus;
import com.bca.core_banking_service.infrastructure.input.dto.Credit.CreditType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.CreditEntity;

class CreditMapperTest {

    @Test
    void toDomain_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CreditEntity entity = new CreditEntity(
                "cred-1",
                "cus-1",
                CreditEntity.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(12.5),
                12,
                CreditEntity.CreditStatus.ACTIVE,
                now);

        Credit credit = CreditMapper.toDomain(entity);

        assertEquals("cred-1", credit.getId());
        assertEquals("cus-1", credit.getCustomerId());
        assertEquals(CreditType.PERSONAL_LOAN, credit.getCreditType());
        assertEquals(BigDecimal.valueOf(900), credit.getPendingDebt());
        assertEquals(CreditStatus.ACTIVE, credit.getStatus());
        assertEquals(now, credit.getCreatedAt());
    }

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Credit credit = new Credit(
                "cred-2",
                "cus-2",
                CreditType.AUTO_LOAN,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(4000),
                BigDecimal.valueOf(18),
                24,
                CreditStatus.PAID,
                now);

        CreditEntity entity = CreditMapper.toEntity(credit);

        assertEquals("cred-2", entity.getId());
        assertEquals("cus-2", entity.getCustomerId());
        assertEquals(CreditEntity.CreditType.AUTO_LOAN, entity.getCreditType());
        assertEquals(BigDecimal.valueOf(4000), entity.getPendingDebt());
        assertEquals(CreditEntity.CreditStatus.PAID, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
    }
}
