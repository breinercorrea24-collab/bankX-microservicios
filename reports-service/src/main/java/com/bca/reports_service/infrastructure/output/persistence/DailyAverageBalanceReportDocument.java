package com.bca.reports_service.infrastructure.output.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "daily_average_balance_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAverageBalanceReportDocument {
    @Id
    private String id; // Format: customerId_productId_date

    private String customerId;
    private String productType;
    private String productId;
    private LocalDate date;
    private Double openingBalance;
    private Double closingBalance;
    private String currency;
}