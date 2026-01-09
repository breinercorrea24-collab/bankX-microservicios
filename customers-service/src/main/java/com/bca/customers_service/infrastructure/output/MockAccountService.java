package com.bca.customers_service.infrastructure.output;

import com.bca.customers_service.domain.dto.AccountSummary;
import com.bca.customers_service.domain.ports.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class MockAccountService implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(MockAccountService.class);

    @Override
    public Flux<AccountSummary> getAccountsByCustomerId(String customerId) {
        logger.info("Fetching accounts for customer ID: {}", customerId);
        // Mock data - in real implementation, this would call account service
        List<AccountSummary> accounts = Arrays.asList(
                new AccountSummary("acc-001", AccountSummary.AccountType.SAVINGS, "PEN", 5500.50, 5300.50, AccountSummary.AccountStatus.ACTIVE),
                new AccountSummary("acc-002", AccountSummary.AccountType.CURRENT, "USD", 1000.00, 1000.00, AccountSummary.AccountStatus.ACTIVE)
        );
        logger.info("Retrieved {} accounts for customer {}", accounts.size(), customerId);
        return Flux.fromIterable(accounts);
    }
}