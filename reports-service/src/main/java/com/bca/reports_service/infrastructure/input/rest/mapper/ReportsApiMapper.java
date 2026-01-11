package com.bca.reports_service.infrastructure.input.rest.mapper;

public class ReportsApiMapper {
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
        List<Product> products = null;
        if (domainReport.getProductBalances() == null) {
            products = List.of();
        } else {
            products = domainReport.getProductBalances().stream()
                    .map(pb -> {
                        Product product = new Product();
                        product.setProductType(pb.getProductType());
                        product.setSubType(pb.getProductType().toLowerCase());
                        product.setProductId(pb.getProductId());
                        product.setAlias(pb.getProductType() + " - " + pb.getProductId());
                        product.setAverageDailyBalance(pb.getAverageDailyBalance());
                        product.setCurrency(pb.getCurrency());
                        product.setStatus(pb.getStatus());
                        return product;
                    })
                    .collect(Collectors.toList());
        }
        dto.setProducts(products);
        // Map totals
        Totals totals = new Totals();
        if (domainReport.getTotals() != null) {
            totals.setAverageAssets(domainReport.getTotals().getAverageAssets());
            totals.setAverageLiabilities(domainReport.getTotals().getAverageLiabilities());
            totals.setNetAveragePosition(domainReport.getTotals().getNetAveragePosition());
        } else {
            // Valores predeterminados en caso de que getTotals() sea null
            totals.setAverageAssets(0.0);
            totals.setAverageLiabilities(0.0);
            totals.setNetAveragePosition(0.0);
        }

        dto.setTotals(totals);

        log.debug("Successfully mapped {} products to DTO", products.size());
        return dto;
    }

}
