package com.bca.reports_service.application.usecases;

import com.bca.reports_service.domain.model.DailyAverageBalanceReport;
import com.bca.reports_service.domain.ports.output.DailyAverageBalanceReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCustomerDailyAverageBalanceUseCase {

    private final DailyAverageBalanceReportRepository dailyAverageBalanceReportRepository;

    public Mono<DailyAverageBalanceReport> execute(String customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting customer daily average balance report for customerId: {}, period: {} to {}",
                customerId, startDate, endDate);
        return dailyAverageBalanceReportRepository.findByCustomerIdAndPeriod(customerId, startDate, endDate)
                .doOnNext(report -> log.info("Successfully retrieved daily average balance report for customer: {} with {} product balances",
                        customerId, report.getProductBalances() != null ? report.getProductBalances().size() : 0))
                .doOnError(error -> log.error("Error retrieving daily average balance report for customerId: {}", customerId, error));
    }
}