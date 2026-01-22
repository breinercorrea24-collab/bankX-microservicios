package com.bca.customers_service.infrastructure.output.persistence.mapper;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.infrastructure.output.persistence.entity.CustomerDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerMapperTest {

    @Test
    void mapToDocument_mapsAllFields() {
        Customer customer = buildDomainCustomer(true);

        CustomerDocument doc = CustomerMapper.mapToDocument(customer);

        assertThat(doc.getId()).isEqualTo("id-1");
        assertThat(doc.getFullName()).isEqualTo("John Doe");
        assertThat(doc.getDocument().getType()).isEqualTo(CustomerDocument.Document.DocumentType.DNI);
        assertThat(doc.getDocument().getNumber()).isEqualTo("12345678");
        assertThat(doc.getPhoneNumber()).isEqualTo("999888777");
        assertThat(doc.getEmail()).isEqualTo("john@example.com");
        assertThat(doc.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(doc.getAddress()).isNotNull();
        assertThat(doc.getAddress().getDistrict()).isEqualTo("Miraflores");
        assertThat(doc.getOccupation()).isEqualTo("Engineer");
        assertThat(doc.getMonthlyIncome()).isEqualTo(5000.0);
        assertThat(doc.getPep()).isFalse();
        assertThat(doc.getCustomerType()).isEqualTo(CustomerDocument.CustomerType.BCP);
        assertThat(doc.getKycLevel()).isEqualTo(CustomerDocument.KycLevel.LIGHT);
        assertThat(doc.getStatus()).isEqualTo(CustomerDocument.CustomerStatus.ACTIVE);
        assertThat(doc.getCreatedAt()).isEqualTo(customer.getCreatedAt());
        assertThat(doc.getUpdatedAt()).isEqualTo(customer.getUpdatedAt());
    }

    @Test
    void mapToDocument_handlesNullAddress() {
        Customer customer = buildDomainCustomer(false);

        CustomerDocument doc = CustomerMapper.mapToDocument(customer);

        assertThat(doc.getAddress()).isNull();
    }

    @Test
    void mapToCustomer_mapsAllFields() {
        CustomerDocument document = buildDocument(true);

        Customer customer = CustomerMapper.mapToCustomer(document);

        assertThat(customer.getId()).isEqualTo("doc-1");
        assertThat(customer.getFullName()).isEqualTo("Jane Roe");
        assertThat(customer.getDocument().getType()).isEqualTo(Customer.Document.DocumentType.CE);
        assertThat(customer.getDocument().getNumber()).isEqualTo("CE-999");
        assertThat(customer.getPhoneNumber()).isEqualTo("111222333");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");
        assertThat(customer.getBirthDate()).isEqualTo(LocalDate.of(1992, 2, 2));
        assertThat(customer.getAddress()).isNotNull();
        assertThat(customer.getAddress().getProvince()).isEqualTo("Cusco");
        assertThat(customer.getOccupation()).isEqualTo("Teacher");
        assertThat(customer.getMonthlyIncome()).isEqualTo(3000.0);
        assertThat(customer.getPep()).isTrue();
        assertThat(customer.getCustomerType()).isEqualTo(Customer.CustomerType.YANKI);
        assertThat(customer.getKycLevel()).isEqualTo(Customer.KycLevel.FULL);
        assertThat(customer.getStatus()).isEqualTo(Customer.CustomerStatus.PENDING_VERIFICATION);
    }

    @Test
    void mapToCustomer_handlesNullAddress() {
        CustomerDocument doc = buildDocument(false);

        Customer customer = CustomerMapper.mapToCustomer(doc);

        assertThat(customer.getAddress()).isNull();
    }

    @Test
    void mapCustomerTypeToDocument_convertsEnums() {
        assertThat(CustomerMapper.mapCustomerTypeToDocument(Customer.CustomerType.YANKI))
                .isEqualTo(CustomerDocument.CustomerType.YAPE);
        assertThat(CustomerMapper.mapCustomerTypeToDocument(Customer.CustomerType.BANKX))
                .isEqualTo(CustomerDocument.CustomerType.BCP);
    }

    @Test
    void mapCustomerTypeFromDocument_convertsEnums() {
        assertThat(CustomerMapper.mapCustomerTypeFromDocument(CustomerDocument.CustomerType.YAPE))
                .isEqualTo(Customer.CustomerType.YANKI);
        assertThat(CustomerMapper.mapCustomerTypeFromDocument(CustomerDocument.CustomerType.BCP))
                .isEqualTo(Customer.CustomerType.BANKX);
    }

    @Test
    void mapCustomerTypeToDocument_throwsOnNull() {
        assertThrows(NullPointerException.class, () -> CustomerMapper.mapCustomerTypeToDocument(null));
    }

    @Test
    void mapCustomerTypeFromDocument_throwsOnNull() {
        assertThrows(NullPointerException.class, () -> CustomerMapper.mapCustomerTypeFromDocument(null));
    }

    private Customer buildDomainCustomer(boolean includeAddress) {
        Customer customer = new Customer();
        customer.setId("id-1");
        customer.setFullName("John Doe");
        customer.setDocument(new Customer.Document(Customer.Document.DocumentType.DNI, "12345678"));
        customer.setPhoneNumber("999888777");
        customer.setEmail("john@example.com");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        if (includeAddress) {
            customer.setAddress(new Customer.Address("Lima", "Lima", "Miraflores", "Av. Example 123"));
        }
        customer.setOccupation("Engineer");
        customer.setMonthlyIncome(5000.0);
        customer.setPep(false);
        customer.setCustomerType(Customer.CustomerType.BANKX);
        customer.setKycLevel(Customer.KycLevel.LIGHT);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setCreatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        customer.setUpdatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        return customer;
    }

    private CustomerDocument buildDocument(boolean includeAddress) {
        CustomerDocument.Document doc = new CustomerDocument.Document(
                CustomerDocument.Document.DocumentType.CE, "CE-999");
        CustomerDocument.Address addr = includeAddress
                ? new CustomerDocument.Address("Cusco", "Cusco", "Cusco", "Calle 123")
                : null;

        return new CustomerDocument(
                "doc-1",
                "Jane Roe",
                doc,
                "111222333",
                "jane@example.com",
                LocalDate.of(1992, 2, 2),
                addr,
                "Teacher",
                3000.0,
                true,
                CustomerDocument.CustomerType.YAPE,
                CustomerDocument.KycLevel.FULL,
                CustomerDocument.CustomerStatus.PENDING_VERIFICATION,
                LocalDateTime.of(2024, 6, 1, 8, 0),
                LocalDateTime.of(2024, 6, 1, 8, 0)
        );
    }
}
