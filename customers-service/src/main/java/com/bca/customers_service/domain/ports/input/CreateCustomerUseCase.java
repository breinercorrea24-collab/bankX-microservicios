package com.bca.customers_service.domain.ports.input;

import com.bca.customers_service.domain.dto.CustomerCreateRequest;
import com.bca.customers_service.domain.model.Customer;

import reactor.core.publisher.Mono;

public interface CreateCustomerUseCase {
    Mono<Customer> execute(CustomerCreateRequest request);
}
