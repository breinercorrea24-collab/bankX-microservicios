package com.bca.customers_service.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocument {
    @Id
    private String id;
    private String fullName;
    private Document document;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private Address address;
    private String occupation;
    private Double monthlyIncome;
    private Boolean pep;
    private CustomerType customerType;
    private KycLevel kycLevel;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CustomerType {
        YAPE, BCP
    }

    public enum KycLevel {
        LIGHT, FULL
    }

    public enum CustomerStatus {
        ACTIVE, PENDING_VERIFICATION, BLOCKED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private DocumentType type;
        private String number;

        public enum DocumentType {
            DNI, CE, PASSPORT
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String department;
        private String province;
        private String district;
        private String street;
    }
}