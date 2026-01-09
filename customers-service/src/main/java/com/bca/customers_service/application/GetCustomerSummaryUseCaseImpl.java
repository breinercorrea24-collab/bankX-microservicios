package com.bca.customers_service.application;

import com.bca.customers_service.domain.ports.AccountService;
import com.bca.customers_service.domain.ports.CreditService;
import com.bca.customers_service.domain.ports.input.GetCustomerSummaryUseCase;
import com.bca.customers_service.domain.dto.AccountSummary;
import com.bca.customers_service.domain.dto.CreditSummary;
import com.bca.customers_service.domain.dto.CustomerSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GetCustomerSummaryUseCaseImpl implements GetCustomerSummaryUseCase{

    private static final Logger logger = LoggerFactory.getLogger(GetCustomerSummaryUseCaseImpl.class);

    private final AccountService accountService;
    private final CreditService creditService;

    public GetCustomerSummaryUseCaseImpl(AccountService accountService, CreditService creditService) {
        this.accountService = accountService;
        this.creditService = creditService;
    }

    public Mono<CustomerSummaryResponse> execute(String customerId) {
        logger.info("Retrieving customer summary for ID: {}", customerId);
        Mono<List<AccountSummary>> accountsMono = accountService.getAccountsByCustomerId(customerId).collectList();
        Mono<List<CreditSummary>> creditsMono = creditService.getCreditsByCustomerId(customerId).collectList();

        return Mono.zip(accountsMono, creditsMono)
                .map(tuple -> {
                    List<AccountSummary> accounts = tuple.getT1();
                    List<CreditSummary> credits = tuple.getT2();

                    double totalBalance = accounts.stream()
                            .mapToDouble(AccountSummary::getBalance)
                            .sum();

                    logger.info("Customer summary calculated for {}: {} accounts, total balance {}", customerId, accounts.size(), totalBalance);
                    return new CustomerSummaryResponse(
                            customerId,
                            accounts.size(),
                            totalBalance,
                            accounts,
                            credits
                    );
                })
                .doOnNext(response -> logger.info("Customer summary retrieved successfully for ID: {}", customerId))
                .doOnError(error -> logger.error("Error retrieving customer summary for {}: {}", customerId, error.getMessage()));
    }
}