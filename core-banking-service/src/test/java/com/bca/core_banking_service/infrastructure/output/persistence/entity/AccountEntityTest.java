package com.bca.core_banking_service.infrastructure.output.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountEntityTest {

    @Test
    @DisplayName("Debe crear una entidad usando el constructor con argumentos y verificar los getters")
    void shouldCreateEntityWithAllArgsConstructor() {
        String id = "acc-123";
        String customerId = "cust-456";
        AccountEntity.AccountType type = AccountEntity.AccountType.SAVINGS;
        String currency = "PEN";
        BigDecimal balance = BigDecimal.valueOf(1500.50);
        AccountEntity.AccountStatus status = AccountEntity.AccountStatus.ACTIVE;
        boolean active = true;

        AccountEntity entity = new AccountEntity(id, customerId, type, currency, balance, status, active);

        assertAll("Verificación de propiedades de la entidad",
            () -> assertEquals(id, entity.getId()),
            () -> assertEquals(customerId, entity.getCustomerId()),
            () -> assertEquals(type, entity.getType()),
            () -> assertEquals(currency, entity.getCurrency()),
            () -> assertEquals(balance, entity.getBalance()),
            () -> assertEquals(status, entity.getStatus()),
            () -> assertTrue(entity.isActive())
        );
    }

    @Test
    @DisplayName("Debe permitir modificar valores usando setters (Lombok @Data)")
    void shouldModifyValuesUsingSetters() {
        AccountEntity entity = new AccountEntity();

        entity.setId("new-id");
        entity.setBalance(BigDecimal.ZERO);
        entity.setType(AccountEntity.AccountType.CURRENT);

        assertEquals("new-id", entity.getId());
        assertEquals(BigDecimal.ZERO, entity.getBalance());
        assertEquals(AccountEntity.AccountType.CURRENT, entity.getType());
    }

    @Test
    @DisplayName("Debe verificar la igualdad de dos entidades con los mismos datos (Equals/HashCode)")
    void testEqualsAndHashCode() {
        AccountEntity entity1 = new AccountEntity("1", "C1", AccountEntity.AccountType.SAVINGS, "USD", BigDecimal.TEN, AccountEntity.AccountStatus.ACTIVE, true);
        AccountEntity entity2 = new AccountEntity("1", "C1", AccountEntity.AccountType.SAVINGS, "USD", BigDecimal.TEN, AccountEntity.AccountStatus.ACTIVE, true);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    @DisplayName("Debe verificar que los Enums tengan los valores correctos")
    void testEnums() {
        assertNotNull(AccountEntity.AccountType.valueOf("SAVINGS"));
        assertNotNull(AccountEntity.AccountType.valueOf("CURRENT"));
        assertNotNull(AccountEntity.AccountStatus.valueOf("ACTIVE"));
        assertNotNull(AccountEntity.AccountStatus.valueOf("INACTIVE"));
    }
}
