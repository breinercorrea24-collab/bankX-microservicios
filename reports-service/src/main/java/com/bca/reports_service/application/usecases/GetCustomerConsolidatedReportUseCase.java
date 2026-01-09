package com.bca.reports_service.application.usecases;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.domain.ports.output.CustomerConsolidateReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCustomerConsolidatedReportUseCase {

    private final CustomerConsolidateReportRepository customerConsolidateReportRepository;

    public Mono<CustomerConsolidateReport> execute(String customerId) {
        log.info("Getting customer consolidated report for customerId: {}", customerId);
        return customerConsolidateReportRepository.findByCustomerId(customerId)
                .doOnNext(report -> log.info("Successfully retrieved customer consolidated report for customer: {}", customerId))
                .doOnError(error -> log.error("Error retrieving customer consolidated report for customerId: {}", customerId, error));
    }
}