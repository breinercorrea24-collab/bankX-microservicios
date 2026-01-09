package com.bca.customers_service.domain.ports;

import com.bca.customers_service.domain.dto.AccountSummary;
import reactor.core.publisher.Flux;

public interface AccountService {
    Flux<AccountSummary> getAccountsByCustomerId(String customerId);
}