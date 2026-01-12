package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.ports.output.CommissionReportRepository;
import com.bca.reports_service.domain.model.CommissionReport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComissionReportRepositoryAdapter implements CommissionReportRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<CommissionReport> findByProductTypeAndProductId(String productType, String productId, LocalDate from,
            LocalDate to) {
        log.info("Starting aggregation for commissions report | productType={} | productId={} | from={} | to={}",
                productType, productId, from, to);

        // Convertir las fechas a Instant
        Instant start = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        log.info("Converted date range to Instant | start={} | end={}", start, end);

        // Crear la operación de coincidencia
        MatchOperation match = Aggregation.match(Criteria.where("productType").is(productType)
                .and("productId").is(productId)
                .and("chargedAt").gte(start).lt(end));

        log.info("Match operation created: {}", match);

        // Crear la operación de agrupación
        GroupOperation group = Aggregation.group("productType", "productId", "accountType", "commissionType")
                .sum("amount").as("totalAmount")
                .count().as("count");

        log.info("Group operation created: {}", group);

        // Crear la operación de proyección
        ProjectionOperation project = Aggregation.project()
                .and("_id.productType").as("productType")
                .and("_id.productId").as("productId")
                .and("_id.accountType").as("accountType")
                .and("_id.commissionType").as("commissionType")
                .and("totalAmount").as("totalAmount")
                .and("count").as("count")
                .andExclude("_id");

        log.info("Projection operation created: {}", project);

        // Crear la agregación
        Aggregation agg = Aggregation.newAggregation(match, group, project);

        log.info("Executing aggregation pipeline on collection: commission_reports");

        // Ejecutar la agregación
        return mongoTemplate.aggregate(agg, "commission_reports", Document.class)
                .collectList()
                .doOnNext(docs -> log.info("Aggregation completed | documents found: {}", docs.size()))
                .map(docs -> {
                    // Calcular el total de comisiones
                    double totalAmount = docs.stream()
                            .mapToDouble(d -> {
                                Object value = d.get("totalAmount");
                                if (value instanceof Number) {
                                    return ((Number) value).doubleValue();
                                }
                                return 0.0; // Valor predeterminado si no es un número
                            })
                            .sum();
                    log.info("Calculated totalAmount: {}", totalAmount);

                    // Determinar la moneda
                    String currency = docs.stream()
                            .map(d -> d.getString("currency"))
                            .filter(c -> c != null)
                            .findFirst()
                            .orElse("PEN");
                    log.info("Determined currency: {}", currency);

                    // Mapear los detalles de las comisiones
                    List<CommissionReport.CommissionDetail> details = docs.stream()
                            .map(d -> new CommissionReport.CommissionDetail(
                                    null,
                                    d.getString("productId"),
                                    d.getString("commissionType"),
                                    d.get("totalAmount") instanceof Number ? ((Number) d.get("totalAmount")).doubleValue() : 0.0,
                                    null))
                            .toList();
                    log.info("Mapped commission details: {}", details);

                    // Crear el reporte de comisiones
                    CommissionReport report = new CommissionReport(
                            productType,
                            productId,
                            new CommissionReport.Period(from, to),
                            totalAmount,
                            currency,
                            details);
                    log.info("Commission report created: {}", report);
                    return report;
                })
                .doOnError(e -> log.error("Error during aggregation for commissions report", e));
    }
}