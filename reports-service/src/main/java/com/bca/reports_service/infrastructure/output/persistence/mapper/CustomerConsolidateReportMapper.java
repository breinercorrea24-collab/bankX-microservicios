package com.bca.reports_service.infrastructure.output.persistence.mapper;

import java.util.stream.Collectors;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.infrastructure.output.persistence.entity.CustomerConsolidateReportDocument;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerConsolidateReportMapper {
    
    public static CustomerConsolidateReport toDomain(CustomerConsolidateReportDocument document) {
        log.debug("Mapping document {} to domain model", document.getId());
        CustomerConsolidateReport report = new CustomerConsolidateReport(
                document.getCustomerId(),
                document.getGeneratedAt(),
                new CustomerConsolidateReport.Customer(
                        document.getCustomer().getName(),
                        document.getCustomer().getDocument(),
                        document.getCustomer().getType()),
                document.getAccounts().stream()
                        .map(acc -> new CustomerConsolidateReport.Account(
                                acc.getAccountId(),
                                acc.getType(),
                                acc.getCurrency(),
                                acc.getBalance()))
                        .collect(Collectors.toList()),
                document.getCards().stream()
                        .map(card -> new CustomerConsolidateReport.Card(
                                card.getCardId(),
                                card.getType(),
                                card.getMaskedNumber(),
                                card.getStatus(),
                                card.getAvailableLimit()))
                        .collect(Collectors.toList()),
                document.getCredits().stream()
                        .map(credit -> new CustomerConsolidateReport.Credit(
                                credit.getCreditId(),
                                credit.getOriginalAmount(),
                                credit.getPendingDebt(),
                                credit.getStatus()))
                        .collect(Collectors.toList()),
                document.getWallets().stream()
                        .map(wallet -> new CustomerConsolidateReport.Wallet(
                                wallet.getWalletId(),
                                wallet.getBalance(),
                                wallet.getCurrency(),
                                wallet.getBalanceBTC()))
                        .collect(Collectors.toMap(
                                CustomerConsolidateReport.Wallet::getWalletId,
                                wallet -> wallet)),
                new CustomerConsolidateReport.Totals(
                        document.getTotals() != null ? document.getTotals().getTotalBalancePEN() : 0.0,
                        document.getTotals() != null ? document.getTotals().getTotalDebt() : 0.0));
        log.debug("Successfully mapped document {} to domain model with {} accounts, {} cards, {} credits, {} wallets",
                document.getId(), report.getAccounts().size(), report.getCards().size(),
                report.getCredits().size(), report.getWallets().size());
        return report;
    }
}
