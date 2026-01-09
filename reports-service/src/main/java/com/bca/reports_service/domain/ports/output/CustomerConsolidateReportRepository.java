package com.bca.reports_service.domain.ports.output;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import reactor.core.publisher.Mono;

public interface CustomerConsolidateReportRepository {
    Mono<CustomerConsolidateReport> findByCustomerId(String customerId);
}