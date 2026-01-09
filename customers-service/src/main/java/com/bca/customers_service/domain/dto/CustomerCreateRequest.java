package com.bca.customers_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateRequest {

    @NotNull
    private CustomerType customerType;

    @NotBlank
    private String fullName;

    @NotNull
    private Document document;

    @NotBlank
    private String phoneNumber;

    private String email;

    private LocalDate birthDate;

    private Address address;

    private String occupation;

    private Double monthlyIncome;

    private Boolean pep;

    public enum CustomerType {
        YANKI, BANKX
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        @NotNull
        private DocumentType type;
        @NotBlank
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