package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.CustomersApiDelegate;
import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.infrastructure.input.mapper.CustomerApiMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerApiDelegateImpl implements CustomersApiDelegate {

    private final AccountUseCase accountUseCase;

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> customersCustomerIdAccountsGet(String customerId,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                accountUseCase.getAccountsByCustomer(customerId)
                        .map(CustomerApiMapper::mapToAccountResponse)));
    }
}
