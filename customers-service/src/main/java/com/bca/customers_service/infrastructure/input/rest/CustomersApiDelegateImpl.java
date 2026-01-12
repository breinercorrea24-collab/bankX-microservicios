package com.bca.customers_service.infrastructure.input.rest;

import com.bca.customers_service.api.CustomersApiDelegate;
import com.bca.customers_service.application.CreateCustomerUseCaseImpl;
import com.bca.customers_service.application.GetCustomerSummaryUseCaseImpl;
import com.bca.customers_service.application.GetCustomerUseCaseImpl;
import com.bca.customers_service.dto.CustomerCreate;
import com.bca.customers_service.dto.CustomerResponse;
import com.bca.customers_service.dto.CustomerSummaryResponse;
import com.bca.customers_service.infrastructure.input.rest.mapper.CustomersApiMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
    public Mono<ResponseEntity<CustomerResponse>> customersCustomerIdGet(String customerId,
            ServerWebExchange exchange) {
        logger.info("Received GET request for customer ID: {}", customerId);
        return getCustomerUseCase.execute(customerId)
                .map(CustomersApiMapper::mapToGeneratedCustomerResponse)
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
    public Mono<ResponseEntity<CustomerSummaryResponse>> customersCustomerIdSummaryGet(String customerId,
            ServerWebExchange exchange) {
        logger.info("Received GET request for customer summary ID: {}", customerId);
        return getCustomerSummaryUseCase.execute(customerId)
                .map(CustomersApiMapper::mapToGeneratedCustomerSummaryResponse)
                .map(response -> {
                    logger.info("Successfully retrieved customer summary: {}", customerId);
                    return ResponseEntity.ok(response);
                })
                .doOnError(error -> logger.error("Error retrieving customer summary for {}: {}", customerId,
                        error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> customersPost(Mono<CustomerCreate> customerCreateMono,
            ServerWebExchange exchange) {
        logger.info("Received POST request to create customer");
        return customerCreateMono
                .map(CustomersApiMapper::mapToCustomerCreateRequest)
                .flatMap(createCustomerUseCase::execute)
                .map(customer -> {
                    logger.info("Customer created successfully with ID: {}", customer.getId());
                    CustomerResponse response = new CustomerResponse();
                    response.setId(customer.getId());
                    response.setFullName(customer.getFullName());
                    response.setDocument(customer.getDocument().getType() + "-" + customer.getDocument().getNumber());
                    response.setCustomerType(CustomersApiMapper.mapCustomerType(customer.getCustomerType()));
                    response.setKycLevel(CustomerResponse.KycLevelEnum.valueOf(customer.getKycLevel().name()));
                    response.setStatus(CustomerResponse.StatusEnum.valueOf(customer.getStatus().name()));
                    response.setCreatedAt(OffsetDateTime.ofInstant(customer.getCreatedAt().toInstant(ZoneOffset.UTC),
                            ZoneOffset.UTC));
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

}
