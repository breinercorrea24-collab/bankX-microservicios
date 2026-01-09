package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.DailyAverageBalanceReport;
import com.bca.reports_service.domain.ports.output.DailyAverageBalanceReportRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DailyAverageBalanceReportRepositoryAdapter
        implements DailyAverageBalanceReportRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public DailyAverageBalanceReportRepositoryAdapter(
            ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<DailyAverageBalanceReport> findByCustomerIdAndPeriod(
            String customerId,
            LocalDate startDate,
            LocalDate endDate) {

        // 🔥 RANGO UTC EXPLÍCITO (ESTO ARREGLA TODO)
        Instant startInstant = startDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Instant endInstant = endDate
                .plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        log.info("""
            Finding daily average balance report
            customerId={}
            startUTC={}
            endUTC={}
            """, customerId, startInstant, endInstant);

        Query query = Query.query(
                Criteria.where("customerId").is(customerId)
                        .and("date").gte(startInstant).lte(endInstant)
        );

        return mongoTemplate.find(query, DailyAverageBalanceReportDocument.class)
                .collectList()
                .doOnNext(docs -> {
                    log.info("Found {} documents for customer {}", docs.size(), customerId);
                    docs.forEach(doc ->
                            log.debug("  - {} | {} | {} | Date={}",
                                    doc.getProductType(),
                                    doc.getProductId(),
                                    doc.getId(),
                                    doc.getDate())
                    );
                })
                .map(this::calculateReport)
                .doOnSuccess(report ->
                        log.info("Daily average balance report generated for customer {}",
                                customerId))
                .doOnError(error ->
                        log.error("Error retrieving documents from MongoDB", error));
    }

    // =========================================================
    // CORE BUSINESS LOGIC
    // =========================================================

    private DailyAverageBalanceReport calculateReport(
            List<DailyAverageBalanceReportDocument> documents) {

        log.info("=== START: calculateReport ===");
        log.info("Total documents received: {}", documents.size());

        if (documents.isEmpty()) {
            log.warn("No documents found for daily average balance report");
            return new DailyAverageBalanceReport();
        }

        String customerId = documents.get(0).getCustomerId();
        log.info("Processing report for customerId: {}", customerId);

        // -----------------------------------------------------
        // 1. GROUP DOCUMENTS BY PRODUCT
        // -----------------------------------------------------
        log.info("--- STEP 1: Grouping documents by product ---");
        Map<String, List<DailyAverageBalanceReportDocument>> documentsByProduct =
                documents.stream()
                        .collect(Collectors.groupingBy(
                                d -> d.getProductType() + "|" + d.getProductId()
                        ));
        
        log.info("Number of unique products: {}", documentsByProduct.size());
        documentsByProduct.forEach((key, docs) -> 
            log.info("  Product: {} | Document count: {}", key, docs.size())
        );

        // -----------------------------------------------------
        // 2. CALCULATE AVERAGE DAILY BALANCE PER PRODUCT
        // -----------------------------------------------------
        log.info("--- STEP 2: Calculating average daily balance per product ---");
        List<DailyAverageBalanceReport.ProductBalance> productBalances =
                documentsByProduct.values().stream()
                        .map(this::calculateProductBalance)
                        .collect(Collectors.toList());
        
        log.info("ProductBalances calculated:");
        productBalances.forEach(pb -> 
            log.info("  {} | {} | Average: {}", pb.getProductType(), pb.getProductId(), pb.getAverageDailyBalance())
        );

        // -----------------------------------------------------
        // 3. CALCULATE TOTALS
        // -----------------------------------------------------
        log.info("--- STEP 3: Calculating totals (Assets vs Liabilities) ---");
        double averageAssets = productBalances.stream()
                .filter(p -> isAsset(p.getProductType()))
                .mapToDouble(DailyAverageBalanceReport.ProductBalance::getAverageDailyBalance)
                .sum();
        
        log.info("Assets breakdown:");
        productBalances.stream()
                .filter(p -> isAsset(p.getProductType()))
                .forEach(asset -> 
                    log.info("  {} ({}): {}", asset.getProductType(), asset.getProductId(), asset.getAverageDailyBalance())
                );
        log.info("Total averageAssets: {}", averageAssets);

        double averageLiabilities = productBalances.stream()
                .filter(p -> isLiability(p.getProductType()))
                .mapToDouble(DailyAverageBalanceReport.ProductBalance::getAverageDailyBalance)
                .sum();
        
        log.info("Liabilities breakdown:");
        productBalances.stream()
                .filter(p -> isLiability(p.getProductType()))
                .forEach(liability -> 
                    log.info("  {} ({}): {}", liability.getProductType(), liability.getProductId(), liability.getAverageDailyBalance())
                );
        log.info("Total averageLiabilities (before abs): {}", averageLiabilities);

        double netAveragePosition = averageAssets + averageLiabilities;
        log.info("netAveragePosition calculation: {} + {} = {}", averageAssets, averageLiabilities, netAveragePosition);

        // -----------------------------------------------------
        // 4. PERIOD
        // -----------------------------------------------------
        log.info("--- STEP 4: Extracting date period ---");
        LocalDate minDate = documents.stream()
                .map(DailyAverageBalanceReportDocument::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate maxDate = documents.stream()
                .map(DailyAverageBalanceReportDocument::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        
        log.info("Period: from {} to {}", minDate, maxDate);

        // -----------------------------------------------------
        // 5. BUILD REPORT
        // -----------------------------------------------------
        log.info("--- STEP 5: Building final report ---");
        DailyAverageBalanceReport report = new DailyAverageBalanceReport(
                customerId,
                new DailyAverageBalanceReport.Period(minDate, maxDate),
                productBalances,
                new DailyAverageBalanceReport.Totals(
                        averageAssets,
                        Math.abs(averageLiabilities),
                        netAveragePosition
                )
        );
        
        log.info("Final Report Summary:");
        log.info("  CustomerId: {}", report.getCustomerId());
        log.info("  Period: {} to {}", report.getPeriod().getStartDate(), report.getPeriod().getEndDate());
        log.info("  Products count: {}", report.getProductBalances().size());
        log.info("  Total Assets: {}", report.getTotals().getAverageAssets());
        log.info("  Total Liabilities (abs): {}", report.getTotals().getAverageLiabilities());
        log.info("  Net Position: {}", report.getTotals().getNetAveragePosition());
        log.info("=== END: calculateReport ===");
        
        return report;
    }

    // =========================================================
    // PRODUCT CALCULATION
    // =========================================================

    private DailyAverageBalanceReport.ProductBalance calculateProductBalance(
            List<DailyAverageBalanceReportDocument> productDocuments) {

        DailyAverageBalanceReportDocument first = productDocuments.get(0);

        log.trace("Calculating balance for product: {} | {}", first.getProductType(), first.getProductId());
        log.trace("Number of documents for this product: {}", productDocuments.size());

        double averageDailyBalance =
                productDocuments.stream()
                        .mapToDouble(doc -> {
                            double dailyAvg = (doc.getOpeningBalance() + doc.getClosingBalance()) / 2;
                            log.trace("  Date: {}, Opening: {}, Closing: {}, Daily Avg: {}",
                                    doc.getDate(), doc.getOpeningBalance(), doc.getClosingBalance(), dailyAvg);
                            return dailyAvg;
                        })
                        .average()
                        .orElse(0.0);
        
        log.trace("Final average for {} | {}: {}", first.getProductType(), first.getProductId(), averageDailyBalance);

        return new DailyAverageBalanceReport.ProductBalance(
                first.getProductType(),
                first.getProductId(),
                averageDailyBalance,
                first.getCurrency(),
                "ACTIVE"
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean isAsset(String productType) {
        return switch (productType) {
            case "BANK_ACCOUNT", "WALLET", "DEBIT_CARD" -> true;
            default -> false;
        };
    }

    private boolean isLiability(String productType) {
        return switch (productType) {
            case "CREDIT", "CREDIT_CARD" -> true;
            default -> false;
        };
    }
}
