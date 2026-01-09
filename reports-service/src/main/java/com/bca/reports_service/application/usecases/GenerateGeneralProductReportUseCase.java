package com.bca.reports_service.application.usecases;

import com.bca.reports_service.domain.model.ProductsReport;
import com.bca.reports_service.domain.ports.output.ProductsReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateGeneralProductReportUseCase {

    private final ProductsReportRepository productsReportRepository;

    public Mono<ProductsReport> execute(String productType, String productId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating general product report for productType: {}, productId: {}, period: {} to {}",
                productType, productId, startDate, endDate);
        return productsReportRepository.findByProductId(productType, productId, startDate, endDate)
                .doOnNext(report -> log.info("Successfully generated product report for product: {}", productId))
                .doOnError(error -> log.error("Error generating product report for productId: {}", productId, error));
    }
}