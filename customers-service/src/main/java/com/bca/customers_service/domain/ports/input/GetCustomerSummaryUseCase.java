package com.bca.customers_service.domain.ports.input;

import com.bca.customers_service.domain.dto.CustomerSummaryResponse;

import reactor.core.publisher.Mono;

public interface GetCustomerSummaryUseCase {
    Mono<CustomerSummaryResponse> execute(String customerId);    
}
