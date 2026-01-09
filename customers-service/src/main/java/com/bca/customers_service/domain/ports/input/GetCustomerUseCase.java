package com.bca.customers_service.domain.ports.input;

import com.bca.customers_service.domain.dto.CustomerResponse;

import reactor.core.publisher.Mono;

public interface GetCustomerUseCase {
    Mono<CustomerResponse> execute(String customerId);
}
