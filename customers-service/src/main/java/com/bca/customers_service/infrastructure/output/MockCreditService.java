package com.bca.customers_service.infrastructure.output;

import com.bca.customers_service.domain.dto.CreditSummary;
import com.bca.customers_service.domain.ports.CreditService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class MockCreditService implements CreditService {

    private static final Logger logger = LoggerFactory.getLogger(MockCreditService.class);

    @Override
    public Flux<CreditSummary> getCreditsByCustomerId(String customerId) {
        logger.info("Fetching credits for customer ID: {}", customerId);
        // Mock data - in real implementation, this would call credit service
        List<CreditSummary> credits = Arrays.asList(
                new CreditSummary("cred-01", 30000.00, 15000.00, "PEN", CreditSummary.CreditStatus.ACTIVE)
        );
        logger.info("Retrieved {} credits for customer {}", credits.size(), customerId);
        return Flux.fromIterable(credits);
    }
}