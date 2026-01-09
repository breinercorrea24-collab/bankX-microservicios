package com.bca.customers_service.domain.ports.output;

import com.bca.customers_service.domain.model.Customer;
import reactor.core.publisher.Mono;

public interface CustomerRepository {
    Mono<Customer> save(Customer customer);
    Mono<Customer> findById(String id);
    Mono<Boolean> existsByDocument(String documentType, String documentNumber);
}