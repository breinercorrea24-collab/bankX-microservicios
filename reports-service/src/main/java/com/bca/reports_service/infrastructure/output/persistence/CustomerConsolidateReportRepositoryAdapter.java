package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.domain.ports.output.CustomerConsolidateReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerConsolidateReportRepositoryAdapter implements CustomerConsolidateReportRepository {

    private final CustomerConsolidateReportMongoRepository mongoRepository;

    @Override
    public Mono<CustomerConsolidateReport> findByCustomerId(String customerId) {
        log.info("Finding customer consolidate report for customerId: {}", customerId);
        return mongoRepository.findByCustomerId(customerId)
                .doOnNext(document -> log.debug("Found customer consolidate document: {}", document.getId()))
                .map(this::toDomain)
                .doOnNext(domain -> log.info("Successfully mapped customer consolidate report for customer: {}",
                        domain.getCustomerId()));
    }

    private CustomerConsolidateReport toDomain(CustomerConsolidateReportDocument document) {
        log.debug("Mapping document {} to domain model", document.getId());
        CustomerConsolidateReport report = new CustomerConsolidateReport(
                document.getCustomerId(),
                document.getGeneratedAt(),
                new CustomerConsolidateReport.Customer(
                        document.getCustomer().getName(),
                        document.getCustomer().getDocument(),
                        document.getCustomer().getType()
                ),
                document.getAccounts().stream()
                        .map(acc -> new CustomerConsolidateReport.Account(
                                acc.getAccountId(),
                                acc.getType(),
                                acc.getCurrency(),
                                acc.getBalance()
                        ))
                        .collect(Collectors.toList()),
                document.getCards().stream()
                        .map(card -> new CustomerConsolidateReport.Card(
                                card.getCardId(),
                                card.getType(),
                                card.getMaskedNumber(),
                                card.getStatus(),
                                card.getAvailableLimit()
                        ))
                        .collect(Collectors.toList()),
                document.getCredits().stream()
                        .map(credit -> new CustomerConsolidateReport.Credit(
                                credit.getCreditId(),
                                credit.getOriginalAmount(),
                                credit.getPendingDebt(),
                                credit.getStatus()
                        ))
                        .collect(Collectors.toList()),
                document.getWallets().stream()
                        .map(wallet -> new CustomerConsolidateReport.Wallet(
                                wallet.getWalletId(),
                                wallet.getBalance(),
                                wallet.getCurrency(),
                                wallet.getBalanceBTC()
                        ))
                        .collect(Collectors.toMap(
                                CustomerConsolidateReport.Wallet::getWalletId,
                                wallet -> wallet
                        )),
                new CustomerConsolidateReport.Totals(
                        document.getTotals() != null ? document.getTotals().getTotalBalancePEN() : 0.0,
                        document.getTotals() != null ? document.getTotals().getTotalDebt() : 0.0
                )
        );
        log.debug("Successfully mapped document {} to domain model with {} accounts, {} cards, {} credits, {} wallets",
                document.getId(), report.getAccounts().size(), report.getCards().size(),
                report.getCredits().size(), report.getWallets().size());
        return report;
    }

}
