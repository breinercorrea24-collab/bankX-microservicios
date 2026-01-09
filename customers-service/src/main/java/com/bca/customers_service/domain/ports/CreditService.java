package com.bca.customers_service.domain.ports;

import com.bca.customers_service.domain.dto.CreditSummary;
import reactor.core.publisher.Flux;

public interface CreditService {
    Flux<CreditSummary> getCreditsByCustomerId(String customerId);
}