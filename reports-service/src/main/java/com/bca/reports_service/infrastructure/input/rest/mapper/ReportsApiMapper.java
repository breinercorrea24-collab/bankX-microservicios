package com.bca.reports_service.infrastructure.input.rest.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import com.bca.reports_service.domain.model.CommissionReport;
import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.domain.model.ProductsReport;
import com.bca.reports_service.domain.model.TransactionReport;
import com.bca.reports_service.dto.CardTransactionReport;
import com.bca.reports_service.dto.CardTransactionReportTransactionsInner;
import com.bca.reports_service.dto.CommissionReportResponse;
import com.bca.reports_service.dto.CommissionReportResponseComissionsInner;
import com.bca.reports_service.dto.CommissionReportResponseSummary;
import com.bca.reports_service.dto.CustomerDailyAverageBalanceReport;
import com.bca.reports_service.dto.CustomerReportResponse;
import com.bca.reports_service.dto.CustomerReportResponseAccountsInner;
import com.bca.reports_service.dto.CustomerReportResponseCardsInner;
import com.bca.reports_service.dto.CustomerReportResponseCreditsInner;
import com.bca.reports_service.dto.CustomerReportResponseCustomer;
import com.bca.reports_service.dto.CustomerReportResponseWalletsInner;
import com.bca.reports_service.dto.ProductGeneralReport;
import com.bca.reports_service.dto.ProductGeneralReportMovementsInner;
import com.bca.reports_service.dto.ProductGeneralReportPeriod;
import com.bca.reports_service.dto.ProductGeneralReportSummary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportsApiMapper {

    // Mapping methods to convert domain models to generated DTOs
    public static CommissionReportResponse mapToCommissionReportResponse(CommissionReport domainReport) {
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

    public static CommissionReportResponseComissionsInner mapCommissionDetail(CommissionReport.CommissionDetail detail) {
        CommissionReportResponseComissionsInner dto = new CommissionReportResponseComissionsInner();
        dto.setProductType("BANK_ACCOUNT"); // Default value
        dto.setProductId(detail.getProductId());
        dto.setCommissionType(detail.getCommissionType());
        dto.setAmount(detail.getAmount().floatValue()); // Convert Double to Float
        dto.setCurrency("PEN"); // Default currency
        return dto;
    }

    public static CustomerReportResponse mapToCustomerReportResponse(CustomerConsolidateReport domainReport) {
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
                    .map(ReportsApiMapper::mapAccount)
                    .collect(Collectors.toList());
            dto.setAccounts(accounts);
        }

        // Map cards
        if (domainReport.getCards() != null) {
            List<CustomerReportResponseCardsInner> cards = domainReport.getCards().stream()
                    .map(ReportsApiMapper::mapCard)
                    .collect(Collectors.toList());
            dto.setCards(cards);
        }

        // Map credits
        if (domainReport.getCredits() != null) {
            List<CustomerReportResponseCreditsInner> credits = domainReport.getCredits().stream()
                    .map(ReportsApiMapper::mapCredit)
                    .collect(Collectors.toList());
            dto.setCredits(credits);
        }

        // Map wallets
        if (domainReport.getWallets() != null) {
            List<CustomerReportResponseWalletsInner> wallets = domainReport.getWallets().values().stream()
                    .map(ReportsApiMapper::mapWallet)
                    .collect(Collectors.toList());
            dto.setWallets(wallets);
        }

        return dto;
    }

    public static CustomerReportResponseAccountsInner mapAccount(CustomerConsolidateReport.Account account) {
        CustomerReportResponseAccountsInner dto = new CustomerReportResponseAccountsInner();
        dto.setAccountId(account.getAccountId());
        dto.setBalance(account.getBalance().floatValue());
        dto.setCurrency(account.getCurrency());
        dto.setType(account.getType());
        return dto;
    }

    public static CustomerReportResponseCardsInner mapCard(CustomerConsolidateReport.Card card) {
        CustomerReportResponseCardsInner dto = new CustomerReportResponseCardsInner();
        dto.setCardId(card.getCardId());
        dto.setMaskedNumber(card.getMaskedNumber());
        dto.setType(card.getType());
        dto.setStatus(card.getStatus());
        return dto;
    }

    public static CustomerReportResponseCreditsInner mapCredit(CustomerConsolidateReport.Credit credit) {
        CustomerReportResponseCreditsInner dto = new CustomerReportResponseCreditsInner();
        dto.setCreditId(credit.getCreditId());
        dto.setOriginalAmount(credit.getOriginalAmount() != null ? credit.getOriginalAmount().floatValue() : 0.0f);
        dto.setPendingDebt(credit.getPendingDebt() != null ? credit.getPendingDebt().floatValue() : 0.0f);
        dto.setCurrency("PEN"); // Default currency since it's missing in domain model
        dto.setType("PERSONAL_LOAN"); // Default type since it's missing in domain model
        dto.setStatus(credit.getStatus());
        return dto;
    }

    public static CustomerReportResponseWalletsInner mapWallet(CustomerConsolidateReport.Wallet wallet) {
        CustomerReportResponseWalletsInner dto = new CustomerReportResponseWalletsInner();
        dto.setWalletId(wallet.getWalletId());
        dto.setBalance(wallet.getBalance() != null ? wallet.getBalance().floatValue() : 0.0f);
        dto.setCurrency(wallet.getCurrency() != null ? wallet.getCurrency() : "PEN");
        dto.setType("DIGITAL_WALLET");
        return dto;
    }

    public static ProductGeneralReport mapToProductGeneralReport(ProductsReport domainReport) {
        ProductGeneralReport dto = new ProductGeneralReport();
        dto.setProductType(domainReport.getProduct().getProductType());
        dto.setProductId(domainReport.getProduct().getProductId());
        dto.setProductSubType("SAVINGS"); // Default value since it's missing in domain model

        // Map period
        ProductGeneralReportPeriod period = new ProductGeneralReportPeriod();
        period.setFrom(domainReport.getPeriod().getStartDate());
        period.setTo(domainReport.getPeriod().getEndDate());
        dto.setPeriod(period);

        // Map summary
        if (domainReport.getSummary() != null) {
            ProductGeneralReportSummary summary = new ProductGeneralReportSummary();
            summary.setTotalMovements(0); // Default value since totalTransactions is not available in domain model
            summary.setTotalAmount(domainReport.getSummary().getTotalCredits() != null
                    ? domainReport.getSummary().getTotalCredits().floatValue()
                    : 0.0f); // Convert Double to Float
            summary.setTotalCommissions(domainReport.getSummary().getTotalCommissions() != null
                    ? domainReport.getSummary().getTotalCommissions().floatValue()
                    : 0.0f); // Convert Double to Float
            dto.setSummary(summary);
        }

        // Map movements (using transactions as movements)
        if (domainReport.getTransactions() != null) {
            List<ProductGeneralReportMovementsInner> movements = domainReport.getTransactions().stream()
                    .map(ReportsApiMapper::mapMovement)
                    .collect(Collectors.toList());
            dto.setMovements(movements);
        }

        return dto;
    }

    public static ProductGeneralReportMovementsInner mapMovement(ProductsReport.Transaction movement) {
        ProductGeneralReportMovementsInner dto = new ProductGeneralReportMovementsInner();
        dto.setDate(movement.getDate());
        dto.setAmount(movement.getAmount().floatValue()); // Convert Double to Float
        dto.setMovementType("DEPOSIT"); // Default value since movementType is missing
        dto.setCurrency("PEN"); // Default currency since it's missing
        return dto;
    }

    public static CardTransactionReport mapToCardTransactionReport(TransactionReport domainReport) {
        CardTransactionReport dto = new CardTransactionReport();
        dto.setCardId(domainReport.getCard().getCardId());
        dto.setCardType(domainReport.getCard().getCardType());
        dto.setMaskedNumber(domainReport.getCard().getMaskedNumber());
        dto.setCurrency(domainReport.getCard().getCurrency());

        // Map transactions
        if (domainReport.getTransactions() != null) {
            List<CardTransactionReportTransactionsInner> transactions = domainReport.getTransactions().stream()
                    .map(ReportsApiMapper::mapTransaction)
                    .collect(Collectors.toList());
            dto.setTransactions(transactions);
        }

        return dto;
    }

    public static CardTransactionReportTransactionsInner mapTransaction(TransactionReport.Transaction transaction) {
        CardTransactionReportTransactionsInner dto = new CardTransactionReportTransactionsInner();
        dto.setDate(OffsetDateTime.of(transaction.getDate(), ZoneOffset.UTC)); // Convert LocalDateTime to
                                                                               // OffsetDateTime
        dto.setAmount(transaction.getAmount().floatValue()); // Convert Double to Float
        dto.setMovementType("WITHDRAWAL"); // Default value since movementType is missing
        dto.setMerchant("Unknown Merchant"); // Default value since merchant is missing
        dto.setCurrency("PEN"); // Default currency since it's missing
        dto.setStatus("APPROVED"); // Default status since it's missing
        return dto;
    }

    public static CustomerDailyAverageBalanceReport mapToCustomerDailyAverageBalanceReportDto(
            com.bca.reports_service.domain.model.DailyAverageBalanceReport domainReport) {
        log.debug("Mapping DailyAverageBalanceReport to CustomerDailyAverageBalanceReport DTO");

        CustomerDailyAverageBalanceReport dto = new CustomerDailyAverageBalanceReport();
        dto.setCustomerId(domainReport.getCustomerId());
        dto.setCustomerName("Unknown Customer"); // Default value since not available in domain model

        LocalDate startDate;

        if (domainReport.getPeriod() == null || domainReport.getPeriod().getStartDate() == null) {
            startDate = LocalDate.now().minusMonths(1);
        } else {
            startDate = domainReport.getPeriod().getStartDate();
        }

        dto.setMonth(startDate.getYear() + "-" + String.format("%02d", startDate.getMonthValue())); // Format as YYYY-MM
        dto.setCurrency("PEN"); // Default currency
        dto.setGeneratedAt(OffsetDateTime.now());

        // Map product balances to Product DTOs
        /* List<Product> products = null;
        if (domainReport.getProductBalances() == null) {
            products = List.of();
        } else {
            products = domainReport.getProductBalances().stream()
                    .map(pb -> {
                        Product product = new Product();
                        product.setProductType(pb.getProductType());
                        product.setProductId(pb.getProductId());
                        product.setCurrency(pb.getCurrency());
                      
                        return product;
                    })
                    .collect(Collectors.toList());
        } */

        dto.setProducts(null);
        // Map totals

        /* Totals totals = new Totals();
        if (domainReport.getTotals() != null) {
            totals.setAverageAssets(domainReport.getTotals().getAverageAssets());
            totals.setAverageLiabilities(domainReport.getTotals().getAverageLiabilities());
            totals.setNetAveragePosition(domainReport.getTotals().getNetAveragePosition());
        } else {
            totals.setAverageAssets(0.0);
            totals.setAverageLiabilities(0.0);
            totals.setNetAveragePosition(0.0);
        } */

        dto.setTotals(null);

      /*   log.debug("Successfully mapped {} products to DTO", products.size()); */
        return dto;
    }

}
