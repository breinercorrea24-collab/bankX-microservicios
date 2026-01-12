package com.bca.customers_service.infrastructure.input.rest.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.dto.AccountSummary;
import com.bca.customers_service.dto.CreditSummary;
import com.bca.customers_service.dto.CustomerCreate;
import com.bca.customers_service.dto.CustomerResponse;
import com.bca.customers_service.dto.CustomerSummaryResponse;

public class CustomersApiMapper {
    public static CustomerResponse mapToGeneratedCustomerResponse(
            com.bca.customers_service.domain.dto.CustomerResponse customResponse) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customResponse.getId());
        response.setFullName(customResponse.getFullName());
        response.setDocument(customResponse.getDocument());
        response.setCustomerType(CustomerResponse.CustomerTypeEnum.valueOf(customResponse.getCustomerType().name()));
        response.setKycLevel(CustomerResponse.KycLevelEnum.valueOf(customResponse.getKycLevel().name()));
        response.setStatus(CustomerResponse.StatusEnum.valueOf(customResponse.getStatus().name()));
        response.setCreatedAt(
                OffsetDateTime.ofInstant(customResponse.getCreatedAt().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
        return response;
    }

    public static CustomerSummaryResponse mapToGeneratedCustomerSummaryResponse(
            com.bca.customers_service.domain.dto.CustomerSummaryResponse customResponse) {
        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setCustomerId(customResponse.getCustomerId());
        response.setTotalAccounts(customResponse.getTotalAccounts());
        response.setTotalBalance(customResponse.getTotalBalance());
        response.setAccounts(customResponse.getAccounts().stream()
                .map(CustomersApiMapper::mapToGeneratedAccountSummary)
                .collect(Collectors.toList()));
        response.setCredits(customResponse.getCredits().stream()
                .map(CustomersApiMapper::mapToGeneratedCreditSummary)
                .collect(Collectors.toList()));
        return response;
    }

    public static AccountSummary mapToGeneratedAccountSummary(
            com.bca.customers_service.domain.dto.AccountSummary custom) {
        AccountSummary summary = new AccountSummary();
        summary.setAccountId(custom.getAccountId());
        summary.setAccountType(AccountSummary.AccountTypeEnum.valueOf(custom.getAccountType().name()));
        summary.setCurrency(AccountSummary.CurrencyEnum.fromValue(custom.getCurrency()));
        summary.setBalance(custom.getBalance());
        summary.setAvailableBalance(custom.getAvailableBalance());
        summary.setStatus(AccountSummary.StatusEnum.valueOf(custom.getStatus().name()));
        return summary;
    }

    public static CreditSummary mapToGeneratedCreditSummary(com.bca.customers_service.domain.dto.CreditSummary custom) {
        CreditSummary summary = new CreditSummary();
        summary.setCreditId(custom.getCreditId());
        summary.setTotalAmount(custom.getTotalAmount());
        summary.setPendingDebt(custom.getPendingDebt());
        summary.setCurrency(CreditSummary.CurrencyEnum.fromValue(custom.getCurrency()));
        summary.setStatus(CreditSummary.StatusEnum.valueOf(custom.getStatus().name()));
        return summary;
    }

    public static com.bca.customers_service.domain.dto.CustomerCreateRequest mapToCustomerCreateRequest(
            CustomerCreate generated) {
        com.bca.customers_service.domain.dto.CustomerCreateRequest request = new com.bca.customers_service.domain.dto.CustomerCreateRequest();
        request.setCustomerType(com.bca.customers_service.domain.dto.CustomerCreateRequest.CustomerType
                .valueOf(generated.getCustomerType().getValue()));
        request.setFullName(generated.getFullName());
        if (generated.getDocument() != null) {
            com.bca.customers_service.domain.dto.CustomerCreateRequest.Document doc = new com.bca.customers_service.domain.dto.CustomerCreateRequest.Document();
            doc.setType(com.bca.customers_service.domain.dto.CustomerCreateRequest.Document.DocumentType
                    .valueOf(generated.getDocument().getType().getValue()));
            doc.setNumber(generated.getDocument().getNumber());
            request.setDocument(doc);
        }
        request.setPhoneNumber(generated.getPhoneNumber());
        request.setEmail(generated.getEmail());
        request.setBirthDate(generated.getBirthDate());
        if (generated.getAddress() != null) {
            com.bca.customers_service.domain.dto.CustomerCreateRequest.Address addr = new com.bca.customers_service.domain.dto.CustomerCreateRequest.Address();
            addr.setDepartment(generated.getAddress().getDepartment());
            addr.setProvince(generated.getAddress().getProvince());
            addr.setDistrict(generated.getAddress().getDistrict());
            addr.setStreet(generated.getAddress().getStreet());
            request.setAddress(addr);
        }
        request.setOccupation(generated.getOccupation());
        request.setMonthlyIncome(generated.getMonthlyIncome());
        request.setPep(generated.getPep());
        return request;
    }

    public static CustomerResponse.CustomerTypeEnum mapCustomerType(Customer.CustomerType type) {
        switch (type) {
            case YANKI:
                return CustomerResponse.CustomerTypeEnum.YANKI;
            case BANKX:
                return CustomerResponse.CustomerTypeEnum.BANKX;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
