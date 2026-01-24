package com.bca.customers_service.infrastructure.input.rest.mapper;

import com.bca.customers_service.domain.dto.AccountSummary;
import com.bca.customers_service.domain.dto.CreditSummary;
import com.bca.customers_service.domain.dto.CustomerCreateRequest;
import com.bca.customers_service.domain.dto.CustomerSummaryResponse;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.dto.CustomerCreate;
import com.bca.customers_service.dto.CustomerCreateAddress;
import com.bca.customers_service.dto.CustomerCreateDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomersApiMapperTest {

    @Test
    void mapToGeneratedCustomerResponse_mapsAllFields() {
        LocalDateTime created = LocalDateTime.of(2024, 12, 1, 10, 30);
        com.bca.customers_service.domain.dto.CustomerResponse domainResponse = new com.bca.customers_service.domain.dto.CustomerResponse(
                "id-1",
                "Jane Doe",
                "DNI-12345678",
                com.bca.customers_service.domain.dto.CustomerResponse.CustomerType.BANKX,
                com.bca.customers_service.domain.dto.CustomerResponse.KycLevel.FULL,
                com.bca.customers_service.domain.dto.CustomerResponse.CustomerStatus.ACTIVE,
                created
        );

        com.bca.customers_service.dto.CustomerResponse generated = CustomersApiMapper.mapToGeneratedCustomerResponse(domainResponse);

        assertThat(generated.getId()).isEqualTo("id-1");
        assertThat(generated.getFullName()).isEqualTo("Jane Doe");
        assertThat(generated.getDocument()).isEqualTo("DNI-12345678");
        assertThat(generated.getCustomerType()).isEqualTo(com.bca.customers_service.dto.CustomerResponse.CustomerTypeEnum.BANKX);
        assertThat(generated.getKycLevel()).isEqualTo(com.bca.customers_service.dto.CustomerResponse.KycLevelEnum.FULL);
        assertThat(generated.getStatus()).isEqualTo(com.bca.customers_service.dto.CustomerResponse.StatusEnum.ACTIVE);
        OffsetDateTime expectedDate = OffsetDateTime.ofInstant(created.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        assertThat(generated.getCreatedAt()).isEqualTo(expectedDate);
    }

    @Test
    void mapToGeneratedCustomerSummaryResponse_mapsCollectionsAndTotals() {
        List<AccountSummary> accounts = List.of(
                new AccountSummary("a1", AccountSummary.AccountType.SAVINGS, "PEN", 100.0, 90.0, AccountSummary.AccountStatus.ACTIVE),
                new AccountSummary("a2", AccountSummary.AccountType.CURRENT, "USD", 200.0, 200.0, AccountSummary.AccountStatus.BLOCKED)
        );
        List<CreditSummary> credits = List.of(
                new CreditSummary("c1", 500.0, 250.0, "USD", CreditSummary.CreditStatus.ACTIVE)
        );
        CustomerSummaryResponse domain = new CustomerSummaryResponse("cust-1", 2, 300.0, accounts, credits);

        com.bca.customers_service.dto.CustomerSummaryResponse generated =
                CustomersApiMapper.mapToGeneratedCustomerSummaryResponse(domain);

        assertThat(generated.getCustomerId()).isEqualTo("cust-1");
        assertThat(generated.getTotalAccounts()).isEqualTo(2);
        assertThat(generated.getTotalBalance()).isEqualTo(300.0);
        assertThat(generated.getAccounts()).hasSize(2);
        assertThat(generated.getAccounts().get(0).getAccountId()).isEqualTo("a1");
        assertThat(generated.getAccounts().get(0).getAccountType()).isEqualTo(com.bca.customers_service.dto.AccountSummary.AccountTypeEnum.SAVINGS);
        assertThat(generated.getAccounts().get(0).getCurrency()).isEqualTo(com.bca.customers_service.dto.AccountSummary.CurrencyEnum.PEN);
        assertThat(generated.getCredits()).hasSize(1);
        assertThat(generated.getCredits().get(0).getCreditId()).isEqualTo("c1");
        assertThat(generated.getCredits().get(0).getStatus()).isEqualTo(com.bca.customers_service.dto.CreditSummary.StatusEnum.ACTIVE);
    }

    @Test
    void mapToGeneratedAccountSummary_mapsAllFields() {
        AccountSummary domain = new AccountSummary("a1", AccountSummary.AccountType.SAVINGS, "USD", 50.0, 40.0, AccountSummary.AccountStatus.ACTIVE);

        com.bca.customers_service.dto.AccountSummary generated = CustomersApiMapper.mapToGeneratedAccountSummary(domain);

        assertThat(generated.getAccountId()).isEqualTo("a1");
        assertThat(generated.getAccountType()).isEqualTo(com.bca.customers_service.dto.AccountSummary.AccountTypeEnum.SAVINGS);
        assertThat(generated.getCurrency()).isEqualTo(com.bca.customers_service.dto.AccountSummary.CurrencyEnum.USD);
        assertThat(generated.getBalance()).isEqualTo(50.0);
        assertThat(generated.getAvailableBalance()).isEqualTo(40.0);
        assertThat(generated.getStatus()).isEqualTo(com.bca.customers_service.dto.AccountSummary.StatusEnum.ACTIVE);
    }

    @Test
    void mapToGeneratedCreditSummary_mapsAllFields() {
        CreditSummary domain = new CreditSummary("c1", 1000.0, 100.0, "PEN", CreditSummary.CreditStatus.CANCELLED);

        com.bca.customers_service.dto.CreditSummary generated = CustomersApiMapper.mapToGeneratedCreditSummary(domain);

        assertThat(generated.getCreditId()).isEqualTo("c1");
        assertThat(generated.getTotalAmount()).isEqualTo(1000.0);
        assertThat(generated.getPendingDebt()).isEqualTo(100.0);
        assertThat(generated.getCurrency()).isEqualTo(com.bca.customers_service.dto.CreditSummary.CurrencyEnum.PEN);
        assertThat(generated.getStatus()).isEqualTo(com.bca.customers_service.dto.CreditSummary.StatusEnum.CANCELLED);
    }

    @Test
    void mapToCustomerCreateRequest_mapsNestedFields() {
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.DNI)
                .number("12345678");
        CustomerCreateAddress address = new CustomerCreateAddress()
                .department("Lima")
                .province("Lima")
                .district("Miraflores")
                .street("Av. Example 123");
        CustomerCreate generated = new CustomerCreate(CustomerCreate.CustomerTypeEnum.BANKX, "John Doe", doc, "999888777")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .occupation("Engineer")
                .monthlyIncome(5000.0)
                .pep(false);

        CustomerCreateRequest request = CustomersApiMapper.mapToCustomerCreateRequest(generated);

        assertThat(request.getCustomerType()).isEqualTo(CustomerCreateRequest.CustomerType.BANKX);
        assertThat(request.getFullName()).isEqualTo("John Doe");
        assertThat(request.getDocument().getType()).isEqualTo(CustomerCreateRequest.Document.DocumentType.DNI);
        assertThat(request.getDocument().getNumber()).isEqualTo("12345678");
        assertThat(request.getPhoneNumber()).isEqualTo("999888777");
        assertThat(request.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(request.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(request.getAddress()).isNotNull();
        assertThat(request.getAddress().getDistrict()).isEqualTo("Miraflores");
        assertThat(request.getOccupation()).isEqualTo("Engineer");
        assertThat(request.getMonthlyIncome()).isEqualTo(5000.0);
        assertThat(request.getPep()).isFalse();
    }

    @Test
    void mapToCustomerCreateRequest_handlesNullOptionals() {
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.CE)
                .number("CE-4321");
        CustomerCreate generated = new CustomerCreate(CustomerCreate.CustomerTypeEnum.YANKI, "Jane Smith", doc, "111222333");
        // no email, no address set

        CustomerCreateRequest request = CustomersApiMapper.mapToCustomerCreateRequest(generated);

        assertThat(request.getAddress()).isNull();
        assertThat(request.getEmail()).isNull();
    }

    @Test
    void mapCustomerType_mapsEnumValues() {
        assertThat(CustomersApiMapper.mapCustomerType(com.bca.customers_service.domain.model.Customer.CustomerType.YANKI))
                .isEqualTo(com.bca.customers_service.dto.CustomerResponse.CustomerTypeEnum.YANKI);
        assertThat(CustomersApiMapper.mapCustomerType(com.bca.customers_service.domain.model.Customer.CustomerType.BANKX))
                .isEqualTo(com.bca.customers_service.dto.CustomerResponse.CustomerTypeEnum.BANKX);
    }

    @Test
    void mapToCustomerCreateRequest_handlesNullDocument() {
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.DNI)
                .number("12345678");
        CustomerCreate generated = new CustomerCreate(CustomerCreate.CustomerTypeEnum.BANKX, "John Doe", null, "999888777")
                .address(new CustomerCreateAddress()
                        .department("Lima")
                        .province("Lima")
                        .district("Miraflores")
                        .street("Av. Example 123"));

        CustomerCreateRequest request = CustomersApiMapper.mapToCustomerCreateRequest(generated);

        assertThat(request.getDocument()).isNull();
        assertThat(request.getFullName()).isEqualTo("John Doe");
        assertThat(request.getAddress()).isNotNull();
    }

    @Test
    void mapToCustomerCreateRequest_handlesNullAddress() {
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.PASSPORT)
                .number("PASS-987654");
        CustomerCreate generated = new CustomerCreate(CustomerCreate.CustomerTypeEnum.YANKI, "Jane Smith", doc, "111222333");

        CustomerCreateRequest request = CustomersApiMapper.mapToCustomerCreateRequest(generated);

        assertThat(request.getAddress()).isNull();
        assertThat(request.getDocument()).isNotNull();
        assertThat(request.getDocument().getNumber()).isEqualTo("PASS-987654");
    }

}
