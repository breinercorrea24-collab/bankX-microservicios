package com.bca.reports_service.domain.ports.output;

import java.time.LocalDate;

import com.bca.reports_service.domain.model.ProductsReport;
import reactor.core.publisher.Mono;

public interface ProductsReportRepository {
    Mono<ProductsReport> findByProductId(String productType, String productId, LocalDate startDate, LocalDate endDate);
}
