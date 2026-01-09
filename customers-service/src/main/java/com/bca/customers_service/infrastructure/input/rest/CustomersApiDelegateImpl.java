package com.bca.customers_service.infrastructure.input.rest;

import com.bca.customers_service.api.CustomersApiDelegate;
import com.bca.customers_service.application.CreateCustomerUseCaseImpl;
import com.bca.customers_service.application.GetCustomerSummaryUseCaseImpl;
import com.bca.customers_service.application.GetCustomerUseCaseImpl;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.dto.AccountSummary;
import com.bca.customers_service.dto.CreditSummary;
import com.bca.customers_service.dto.CustomerCreate;
import com.bca.customers_service.dto.CustomerResponse;
import com.bca.customers_service.dto.CustomerSummaryResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CustomersApiDelegateImpl implements CustomersApiDelegate {

    private static final Logger logger = LoggerFactory.getLogger(CustomersApiDelegateImpl.class);

    private final CreateCustomerUseCaseImpl createCustomerUseCase;
    private final GetCustomerUseCaseImpl getCustomerUseCase;
    private final GetCustomerSummaryUseCaseImpl getCustomerSummaryUseCase;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> customersCustomerIdGet(String customerId, ServerWebExchange exchange) {
        logger.info("Received GET request for customer ID: {}", customerId);
        return getCustomerUseCase.execute(customerId)
                .map(this::mapToGeneratedCustomerResponse)
                .map(response -> {
                    logger.info("Successfully retrieved customer: {}", customerId);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("not found")) {
                        logger.warn("Customer not found: {}", customerId);
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    logger.error("Error retrieving customer {}: {}", customerId, ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Override
    public Mono<ResponseEntity<CustomerSummaryResponse>> customersCustomerIdSummaryGet(String customerId, ServerWebExchange exchange) {
        logger.info("Received GET request for customer summary ID: {}", customerId);
        return getCustomerSummaryUseCase.execute(customerId)
                .map(this::mapToGeneratedCustomerSummaryResponse)
                .map(response -> {
                    logger.info("Successfully retrieved customer summary: {}", customerId);
                    return ResponseEntity.ok(response);
                })
                .doOnError(error -> logger.error("Error retrieving customer summary for {}: {}", customerId, error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> customersPost(Mono<CustomerCreate> customerCreateMono, ServerWebExchange exchange) {
        logger.info("Received POST request to create customer");
        return customerCreateMono
                .map(this::mapToCustomerCreateRequest)
                .flatMap(createCustomerUseCase::execute)
                .map(customer -> {
                    logger.info("Customer created successfully with ID: {}", customer.getId());
                    CustomerResponse response = new CustomerResponse();
                    response.setId(customer.getId());
                    response.setFullName(customer.getFullName());
                    response.setDocument(customer.getDocument().getType() + "-" + customer.getDocument().getNumber());
                    response.setCustomerType(mapCustomerType(customer.getCustomerType()));
                    response.setKycLevel(CustomerResponse.KycLevelEnum.valueOf(customer.getKycLevel().name()));
                    response.setStatus(CustomerResponse.StatusEnum.valueOf(customer.getStatus().name()));
                    response.setCreatedAt(OffsetDateTime.ofInstant(customer.getCreatedAt().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    if (ex.getMessage().contains("already exists")) {
                        logger.warn("Attempted to create customer with existing document");
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                    }
                    logger.error("Error creating customer: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    private CustomerResponse mapToGeneratedCustomerResponse(com.bca.customers_service.domain.dto.CustomerResponse customResponse) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customResponse.getId());
        response.setFullName(customResponse.getFullName());
        response.setDocument(customResponse.getDocument());
        response.setCustomerType(CustomerResponse.CustomerTypeEnum.valueOf(customResponse.getCustomerType().name()));
        response.setKycLevel(CustomerResponse.KycLevelEnum.valueOf(customResponse.getKycLevel().name()));
        response.setStatus(CustomerResponse.StatusEnum.valueOf(customResponse.getStatus().name()));
        response.setCreatedAt(OffsetDateTime.ofInstant(customResponse.getCreatedAt().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
        return response;
    }

    private CustomerSummaryResponse mapToGeneratedCustomerSummaryResponse(com.bca.customers_service.domain.dto.CustomerSummaryResponse customResponse) {
        CustomerSummaryResponse response = new CustomerSummaryResponse();
        response.setCustomerId(customResponse.getCustomerId());
        response.setTotalAccounts(customResponse.getTotalAccounts());
        response.setTotalBalance(customResponse.getTotalBalance());
        response.setAccounts(customResponse.getAccounts().stream()
                .map(this::mapToGeneratedAccountSummary)
                .collect(Collectors.toList()));
        response.setCredits(customResponse.getCredits().stream()
                .map(this::mapToGeneratedCreditSummary)
                .collect(Collectors.toList()));
        return response;
    }

    private AccountSummary mapToGeneratedAccountSummary(com.bca.customers_service.domain.dto.AccountSummary custom) {
        AccountSummary summary = new AccountSummary();
        summary.setAccountId(custom.getAccountId());
        summary.setAccountType(AccountSummary.AccountTypeEnum.valueOf(custom.getAccountType().name()));
        summary.setCurrency(AccountSummary.CurrencyEnum.fromValue(custom.getCurrency()));
        summary.setBalance(custom.getBalance());
        summary.setAvailableBalance(custom.getAvailableBalance());
        summary.setStatus(AccountSummary.StatusEnum.valueOf(custom.getStatus().name()));
        return summary;
    }

    private CreditSummary mapToGeneratedCreditSummary(com.bca.customers_service.domain.dto.CreditSummary custom) {
        CreditSummary summary = new CreditSummary();
        summary.setCreditId(custom.getCreditId());
        summary.setTotalAmount(custom.getTotalAmount());
        summary.setPendingDebt(custom.getPendingDebt());
        summary.setCurrency(CreditSummary.CurrencyEnum.fromValue(custom.getCurrency()));
        summary.setStatus(CreditSummary.StatusEnum.valueOf(custom.getStatus().name()));
        return summary;
    }

    private com.bca.customers_service.domain.dto.CustomerCreateRequest mapToCustomerCreateRequest(CustomerCreate generated) {
        com.bca.customers_service.domain.dto.CustomerCreateRequest request = new com.bca.customers_service.domain.dto.CustomerCreateRequest();
        request.setCustomerType(com.bca.customers_service.domain.dto.CustomerCreateRequest.CustomerType.valueOf(generated.getCustomerType().getValue()));
        request.setFullName(generated.getFullName());
        if (generated.getDocument() != null) {
            com.bca.customers_service.domain.dto.CustomerCreateRequest.Document doc = new com.bca.customers_service.domain.dto.CustomerCreateRequest.Document();
            doc.setType(com.bca.customers_service.domain.dto.CustomerCreateRequest.Document.DocumentType.valueOf(generated.getDocument().getType().getValue()));
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

    private CustomerResponse.CustomerTypeEnum mapCustomerType(Customer.CustomerType type) {
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
