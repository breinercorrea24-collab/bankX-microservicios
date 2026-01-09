package com.bca.customers_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
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
        YANKI, BANKX
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