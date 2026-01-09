package com.bca.reports_service.application.usecases;

import com.bca.reports_service.domain.model.CommissionReport;
import com.bca.reports_service.domain.ports.output.CommissionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCommissionsReportUseCase {

    private final CommissionReportRepository commissionReportRepository;

    public Mono<CommissionReport> execute(String productType, String productId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting commissions report for productType: {}, productId: {}, period: {} to {}",
                productType, productId, startDate, endDate);
        return commissionReportRepository.findByProductTypeAndProductId(productType, productId, startDate, endDate)
            .doOnNext(report -> log.info("Successfully retrieved commission report for product: {}", productId))
            .doOnError(error -> log.error("Error retrieving commission report for productId: {}", productId, error));
    }
}