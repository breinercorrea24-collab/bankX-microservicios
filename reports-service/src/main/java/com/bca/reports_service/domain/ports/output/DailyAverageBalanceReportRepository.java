package com.bca.reports_service.domain.ports.output;

import com.bca.reports_service.domain.model.DailyAverageBalanceReport;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface DailyAverageBalanceReportRepository {
    Mono<DailyAverageBalanceReport> findByCustomerIdAndPeriod(String customerId, LocalDate startDate, LocalDate endDate);
}