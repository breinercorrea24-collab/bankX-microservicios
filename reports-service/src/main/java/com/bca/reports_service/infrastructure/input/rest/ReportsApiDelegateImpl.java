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
                    return ResponseEntity.ok(ReportsApiMapper.mapToCommissionReportResponse(report));
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
                    return ResponseEntity.ok(ReportsApiMapper.mapToCustomerReportResponse(report));
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
                .map((ProductsReport report) -> {
                    log.info("Successfully processed general product report request for product: {}", productId);
                    return ResponseEntity.ok(ReportsApiMapper.mapToProductGeneralReport(report));
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
                .map((TransactionReport report) -> {
                    log.info("Successfully processed card transactions report request for card: {}", cardId);
                    return ResponseEntity.ok(ReportsApiMapper.mapToCardTransactionReport(report));
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
                    return ResponseEntity.ok(ReportsApiMapper.mapToCustomerDailyAverageBalanceReportDto(report));
                })
                .doOnError(error -> log.error(
                        "Error processing customer daily average balance report request for customerId: {}", customerId,
                        error));
    }

    

}
