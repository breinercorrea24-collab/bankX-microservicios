package com.bca.customers_service.infrastructure.output.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDocumentTest {

    @Test
    void allArgsConstructor_setsAllFields() {
        CustomerDocument.Document document = new CustomerDocument.Document(
                CustomerDocument.Document.DocumentType.DNI, "12345678");
        CustomerDocument.Address address = new CustomerDocument.Address(
                "Lima", "Lima", "Miraflores", "Av. Example 123");

        LocalDateTime now = LocalDateTime.of(2024, 5, 1, 10, 0);

        CustomerDocument entity = new CustomerDocument(
                "id-1",
                "John Doe",
                document,
                "999888777",
                "john@example.com",
                LocalDate.of(1990, 1, 1),
                address,
                "Engineer",
                5000.0,
                false,
                CustomerDocument.CustomerType.BCP,
                CustomerDocument.KycLevel.LIGHT,
                CustomerDocument.CustomerStatus.ACTIVE,
                now,
                now
        );

        assertThat(entity.getId()).isEqualTo("id-1");
        assertThat(entity.getFullName()).isEqualTo("John Doe");
        assertThat(entity.getDocument()).isSameAs(document);
        assertThat(entity.getAddress()).isSameAs(address);
        assertThat(entity.getOccupation()).isEqualTo("Engineer");
        assertThat(entity.getMonthlyIncome()).isEqualTo(5000.0);
        assertThat(entity.getPep()).isFalse();
        assertThat(entity.getCustomerType()).isEqualTo(CustomerDocument.CustomerType.BCP);
        assertThat(entity.getKycLevel()).isEqualTo(CustomerDocument.KycLevel.LIGHT);
        assertThat(entity.getStatus()).isEqualTo(CustomerDocument.CustomerStatus.ACTIVE);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void settersAndGetters_workForNestedTypes() {
        CustomerDocument entity = new CustomerDocument();
        entity.setId("id-2");
        entity.setFullName("Jane Roe");
        entity.setDocument(new CustomerDocument.Document(CustomerDocument.Document.DocumentType.CE, "CE-999"));
        entity.setAddress(new CustomerDocument.Address("Cusco", "Cusco", "Cusco", "Calle 123"));
        entity.setCustomerType(CustomerDocument.CustomerType.YAPE);
        entity.setKycLevel(CustomerDocument.KycLevel.FULL);
        entity.setStatus(CustomerDocument.CustomerStatus.PENDING_VERIFICATION);

        assertThat(entity.getId()).isEqualTo("id-2");
        assertThat(entity.getDocument().getType()).isEqualTo(CustomerDocument.Document.DocumentType.CE);
        assertThat(entity.getAddress().getProvince()).isEqualTo("Cusco");
        assertThat(entity.getCustomerType()).isEqualTo(CustomerDocument.CustomerType.YAPE);
        assertThat(entity.getKycLevel()).isEqualTo(CustomerDocument.KycLevel.FULL);
        assertThat(entity.getStatus()).isEqualTo(CustomerDocument.CustomerStatus.PENDING_VERIFICATION);
    }

    @Test
    void equalsAndHashCode_considerFields() {
        CustomerDocument.Document doc1 = new CustomerDocument.Document(CustomerDocument.Document.DocumentType.DNI, "1");
        CustomerDocument.Address addr1 = new CustomerDocument.Address("D1","P1","Dist1","Street 1");
        LocalDateTime ts = LocalDateTime.of(2024, 1, 1, 12, 0);

        CustomerDocument a = new CustomerDocument("same", "Name", doc1, "phone", "email",
                LocalDate.of(1990,1,1), addr1, "Occ", 1000.0, true,
                CustomerDocument.CustomerType.YAPE, CustomerDocument.KycLevel.LIGHT,
                CustomerDocument.CustomerStatus.ACTIVE, ts, ts);

        CustomerDocument b = new CustomerDocument("same", "Name", doc1, "phone", "email",
                LocalDate.of(1990,1,1), addr1, "Occ", 1000.0, true,
                CustomerDocument.CustomerType.YAPE, CustomerDocument.KycLevel.LIGHT,
                CustomerDocument.CustomerStatus.ACTIVE, ts, ts);

        CustomerDocument c = new CustomerDocument("other", "Name", doc1, "phone", "email",
                LocalDate.of(1990,1,1), addr1, "Occ", 1000.0, true,
                CustomerDocument.CustomerType.YAPE, CustomerDocument.KycLevel.LIGHT,
                CustomerDocument.CustomerStatus.ACTIVE, ts, ts);

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
    }
}
