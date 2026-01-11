package com.bca.reports_service.infrastructure.input.rest;

import com.bca.reports_service.api.ReportsApiDelegate;
import com.bca.reports_service.application.usecases.*;
import com.bca.reports_service.domain.model.*;
import com.bca.reports_service.dto.*;
import com.bca.reports_service.infrastructure.input.rest.mapper.ReportsApiMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsApiDelegateImpl implements ReportsApiDelegate {

    private final GetCommissionsReportUseCase getCommissionsReportUseCase;
    private final GetCustomerConsolidatedReportUseCase getCustomerConsolidatedReportUseCase;
    private final GenerateGeneralProductReportUseCase generateGeneralProductReportUseCase;
    private final GetCardTransactionsUseCase getCardTransactionsUseCase;
    private final GetCustomerDailyAverageBalanceUseCase getCustomerDailyAverageBalanceUseCase;

    @Override
    public Mono<ResponseEntity<CommissionReportResponse>> reportsCommissionsGet(String productType,
            LocalDate startDate,
            LocalDate endDate,
            String productId,
            ServerWebExchange exchange) {

        log.info("Received request for commission report: productType={}, productId={}, startDate={}, endDate={}",
                productType, productId, startDate, endDate);
        return getCommissionsReportUseCase
                .execute(productType, productId != null ? productId.toString() : null, startDate, endDate)
                .map(report -> {
                    log.info("Successfully processed commission report request for product: {}", productId);
                    return ResponseEntity.ok(mapToCommissionReportResponse(report));
                })
                .doOnError(error -> log.error("Error processing commission report request for productId: {}", productId,
                        error));
    }

    @Override
    public Mono<ResponseEntity<CustomerReportResponse>> reportsCustomerCustomerIdGet(
            String customerId,
            ServerWebExchange exchange) {

        log.info("Received request for customer consolidated report: customerId={}", customerId);
        return getCustomerConsolidatedReportUseCase.execute(customerId)
                .map(report -> {
                    log.info("Successfully processed customer consolidated report request for customer: {}",
                            customerId);
                    return ResponseEntity.ok(mapToCustomerReportResponse(report));
                })
                .doOnError(error -> log.error(
                        "Error processing customer consolidated report request for customerId: {}", customerId, error));
    }

    @Override
    public Mono<ResponseEntity<ProductGeneralReport>> reportsProductsGeneralGet(
            String productType,
            String productId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {

        log.info("Received request for general product report: productType={}, productId={}, startDate={}, endDate={}",
                productType, productId, startDate, endDate);
        return generateGeneralProductReportUseCase.execute(productType, productId.toString(), startDate, endDate)
                .map(report -> {
                    log.info("Successfully processed general product report request for product: {}", productId);
                    return ResponseEntity.ok(mapToProductGeneralReport(report));
                })
                .doOnError(error -> log.error("Error processing general product report request for productId: {}",
                        productId, error));
    }

    @Override
    public Mono<ResponseEntity<CardTransactionReport>> reportsCardsCardIdTransactionsGet(
            String cardId,
            String cardType,
            ServerWebExchange exchange) {

        log.info("Received request for card transactions report: cardId={}, cardType={}", cardId, cardType);
        return getCardTransactionsUseCase.execute(cardId.toString())
                .map(report -> {
                    log.info("Successfully processed card transactions report request for card: {}", cardId);
                    return ResponseEntity.ok(mapToCardTransactionReport(report));
                })
                .doOnError(error -> log.error("Error processing card transactions report request for cardId: {}",
                        cardId, error));
    }

    @Override
    public Mono<ResponseEntity<CustomerDailyAverageBalanceReport>> reportsCustomerCustomerIdDailyAverageBalanceGet(
            String customerId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {

        log.info("Received request for customer daily average balance report: customerId={}, startDate={}, endDate={}",
                customerId, startDate, endDate);
        return getCustomerDailyAverageBalanceUseCase.execute(customerId, startDate, endDate)
                .map(report -> {
                    log.info("Successfully processed customer daily average balance report request for customer: {}",
                            customerId);
                    return ResponseEntity.ok(mapToCustomerDailyAverageBalanceReportDto(report));
                })
                .doOnError(error -> log.error(
                        "Error processing customer daily average balance report request for customerId: {}", customerId,
                        error));
    }

    // Mapping methods to convert domain models to generated DTOs
    private CommissionReportResponse mapToCommissionReportResponse(CommissionReport domainReport) {
        CommissionReportResponse dto = new CommissionReportResponse();
        dto.setCustomerId(domainReport.getProductId()); // Using productId as customerId for now

        // Map period using generated DTO
        ProductGeneralReportPeriod period = new ProductGeneralReportPeriod();
        period.from(domainReport.getPeriod().getStartDate());
        period.to(domainReport.getPeriod().getEndDate());
        dto.setPeriod(null);

        if (domainReport.getDetails() != null) {
            // Map commissions
            List<CommissionReportResponseComissionsInner> commissions = domainReport.getDetails().stream()
                    .map(ReportsApiMapper::mapCommissionDetail)
                    .collect(Collectors.toList());
            dto.setComissions(commissions);

            // Map summary
            CommissionReportResponseSummary summary = new CommissionReportResponseSummary();
            summary.setTotalAmount(domainReport.getTotalCommissionAmount().floatValue()); // Convert Double to Float
            summary.setCurrency(domainReport.getCurrency());
            summary.setTotalCommissions(domainReport.getDetails() != null ? domainReport.getDetails().size() : 0);
            dto.setSummary(summary);
        }

        return dto;
    }

    private CommissionReportResponseComissionsInner mapCommissionDetail(CommissionReport.CommissionDetail detail) {
        CommissionReportResponseComissionsInner dto = new CommissionReportResponseComissionsInner();
        dto.setProductType("BANK_ACCOUNT"); // Default value
        dto.setProductId(detail.getProductId());
        dto.setCommissionType(detail.getCommissionType());
        dto.setAmount(detail.getAmount().floatValue()); // Convert Double to Float
        dto.setCurrency("PEN"); // Default currency
        return dto;
    }

    private CustomerReportResponse mapToCustomerReportResponse(CustomerConsolidateReport domainReport) {
        CustomerReportResponse dto = new CustomerReportResponse();
        dto.setCustomerId(domainReport.getCustomerId());
        dto.setGeneratedAt(OffsetDateTime.now()); // Convert LocalDateTime to OffsetDateTime

        // Map customer
        if (domainReport.getCustomer() != null) {
            CustomerReportResponseCustomer customer = new CustomerReportResponseCustomer();
            customer.setName(domainReport.getCustomer().getName());
            customer.setDocument(domainReport.getCustomer().getDocument());
            customer.setType(domainReport.getCustomer().getType());
            dto.setCustomer(customer);
        }

        // Map accounts
        if (domainReport.getAccounts() != null) {
            List<CustomerReportResponseAccountsInner> accounts = domainReport.getAccounts().stream()
                    .map(this::mapAccount)
                    .collect(Collectors.toList());
            dto.setAccounts(accounts);
        }

        // Map cards
        if (domainReport.getCards() != null) {
            List<CustomerReportResponseCardsInner> cards = domainReport.getCards().stream()
                    .map(this::mapCard)
                    .collect(Collectors.toList());
            dto.setCards(cards);
        }

        // Map credits
        if (domainReport.getCredits() != null) {
            List<CustomerReportResponseCreditsInner> credits = domainReport.getCredits().stream()
                    .map(this::mapCredit)
                    .collect(Collectors.toList());
            dto.setCredits(credits);
        }

        // Map wallets
        if (domainReport.getWallets() != null) {
            List<CustomerReportResponseWalletsInner> wallets = domainReport.getWallets().values().stream()
                    .map(this::mapWallet)
                    .collect(Collectors.toList());
            dto.setWallets(wallets);
        }

        return dto;
    }

}
