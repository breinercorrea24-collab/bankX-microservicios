package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.ProductsReport;
import com.bca.reports_service.domain.model.ProductsReportAggregationDocument;
import com.bca.reports_service.domain.ports.output.ProductsReportRepository;
import com.mongodb.BasicDBObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductsReportRepositoryAdapter implements ProductsReportRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ProductsReport> findByProductId(
            String productType,
            String productId,
            LocalDate startDate,
            LocalDate endDate) {

        log.info(
                "Aggregating product activity | productType={} | productId={} | from={} | to={}",
                productType, productId, startDate, endDate);

        Date from = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(endDate.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        Aggregation aggregation = Aggregation.newAggregation(

                // --------------------------------------------------
                // 1️⃣ MATCH
                // --------------------------------------------------
                Aggregation.match(
                        Criteria.where("productType").is(productType)
                                .and("productId").is(productId)
                                .and("transactionDate").gte(from).lt(to)),

                // --------------------------------------------------
                // 2️⃣ SORT (latest first)
                // --------------------------------------------------
                Aggregation.sort(Sort.Direction.DESC, "transactionDate"),

                // --------------------------------------------------
                // 3️⃣ GROUP
                // --------------------------------------------------
                Aggregation.group("productType", "productId", "subType")
                        .count().as("totalMovements")
                        .sum("amount").as("totalAmount")
                        .sum("commission").as("totalCommissions")
                        .push(
                                new BasicDBObject("date", "$transactionDate")
                                        .append("movementType", "$movementType")
                                        .append("amount", "$amount")
                                        .append("currency", "$currency"))
                        .as("movements"),

                // --------------------------------------------------
                // 4️⃣ PROJECT
                // --------------------------------------------------
                Aggregation.project()
                        .andExclude("_id")
                        .and("_id.productType").as("productType")
                        .and("_id.productId").as("productId")
                        .and("_id.subType").as("subType")
                        .and("totalMovements").as("summary.totalMovements")
                        .and("totalAmount").as("summary.totalAmount")
                        .and("totalCommissions").as("summary.totalCommissions")
                        .and("movements").as("movements"));

        return mongoTemplate.aggregate(
                aggregation,
                "products_reports",
                ProductsReportAggregationDocument.class)
                .next()
                .map(agg -> toDomain(agg, startDate, endDate))
                .doOnSuccess(r -> log.info("Products report generated | productId={}", productId))
                .doOnError(e -> log.error("Error generating products report", e));
    }

}
