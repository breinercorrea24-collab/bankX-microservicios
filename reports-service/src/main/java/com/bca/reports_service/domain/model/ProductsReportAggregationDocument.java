package com.bca.reports_service.domain.model;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsReportAggregationDocument {

    private String productType;
    private String productId;
    private String subType;

    private Summary summary;
    private List<Movement> movements;

    @Data
    public static class Summary {
        private Integer totalMovements;
        private Double totalAmount;
        private Double totalCommissions;
    }

    @Data
    public static class Movement {
        private Date date;
        private String movementType;
        private Double amount;
        private Double commission;
    }
}
