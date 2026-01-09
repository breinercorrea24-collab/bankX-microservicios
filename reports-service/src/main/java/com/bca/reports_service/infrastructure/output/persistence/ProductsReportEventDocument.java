package com.bca.reports_service.infrastructure.output.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Document(collection = "products_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsReportEventDocument {
    @Id
    private String eventId;

    private String productType;
    private String productId;
    private String customerId;

    private String subType;
    private String movementType;

    private Double amount;
    private String currency;
    private Double commission;

    private Date transactionDate;
}