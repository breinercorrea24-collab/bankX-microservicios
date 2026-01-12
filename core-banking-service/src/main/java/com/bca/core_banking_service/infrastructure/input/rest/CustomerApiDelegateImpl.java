package com.bca.core_banking_service.infrastructure.input.rest;

import com.bca.core_banking_service.api.CustomersApiDelegate;
import com.bca.core_banking_service.domain.ports.input.AccountUseCase;
import com.bca.core_banking_service.dto.AccountResponse;
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
                        .map(this::mapToAccountResponse)));
    }

    private AccountResponse mapToAccountResponse(com.bca.core_banking_service.domain.model.Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setType(AccountResponse.TypeEnum.valueOf(account.getType().name()));
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance().floatValue());
        response.setStatus(AccountResponse.StatusEnum.valueOf(account.getStatus().name()));
        return response;
    }
}
