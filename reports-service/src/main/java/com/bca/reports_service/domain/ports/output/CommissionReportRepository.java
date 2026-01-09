package com.bca.reports_service.domain.ports.output;

import java.time.LocalDate;

import com.bca.reports_service.domain.model.CommissionReport;

import reactor.core.publisher.Mono;

public interface CommissionReportRepository {
    Mono<CommissionReport> findByProductTypeAndProductId(String productType, String productId, LocalDate from, LocalDate to);
}