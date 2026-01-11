package com.bca.reports_service.infrastructure.output.persistence.mapper;

import com.bca.reports_service.domain.model.ProductsReport;
import com.bca.reports_service.domain.model.ProductsReportAggregationDocument;

public class ProductsReportMapper {

    public static ProductsReport toDomain(
            ProductsReportAggregationDocument agg,
            LocalDate startDate,
            LocalDate endDate) {

        ProductsReport.Product product = new ProductsReport.Product(
                agg.getProductId(),
                agg.getProductType(),
                null,
                null);

        ProductsReport.Period period = new ProductsReport.Period(
                startDate,
                endDate);

        ProductsReport.Summary summary = new ProductsReport.Summary(
                agg.getSummary().getTotalMovements(),
                agg.getSummary().getTotalAmount(),
                agg.getSummary().getTotalCommissions());

        var transactions = agg.getMovements()
                .stream()
                .map(mov -> new ProductsReport.Transaction(
                        null,
                        mov.getMovementType(),
                        mov.getAmount(),
                        mov.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        null))
                .collect(Collectors.toList());

        return new ProductsReport(
                product,
                period,
                summary,
                transactions,
                null,
                null);
    }
}
