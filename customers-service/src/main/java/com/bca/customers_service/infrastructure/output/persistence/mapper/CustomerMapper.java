package com.bca.customers_service.infrastructure.output.persistence.mapper;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.infrastructure.output.persistence.entity.CustomerDocument;

public class CustomerMapper {
    
    public static CustomerDocument mapToDocument(Customer customer) {
        return new CustomerDocument(
                customer.getId(),
                customer.getFullName(),
                new CustomerDocument.Document(
                        CustomerDocument.Document.DocumentType.valueOf(customer.getDocument().getType().name()),
                        customer.getDocument().getNumber()),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getBirthDate(),
                customer.getAddress() != null ? new CustomerDocument.Address(
                        customer.getAddress().getDepartment(),
                        customer.getAddress().getProvince(),
                        customer.getAddress().getDistrict(),
                        customer.getAddress().getStreet()) : null,
                customer.getOccupation(),
                customer.getMonthlyIncome(),
                customer.getPep(),
                mapCustomerTypeToDocument(customer.getCustomerType()),
                CustomerDocument.KycLevel.valueOf(customer.getKycLevel().name()),
                CustomerDocument.CustomerStatus.valueOf(customer.getStatus().name()),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }

    public static Customer mapToCustomer(CustomerDocument document) {
        return new Customer(
                document.getId(),
                document.getFullName(),
                new Customer.Document(Customer.Document.DocumentType.valueOf(document.getDocument().getType().name()),
                        document.getDocument().getNumber()),
                document.getPhoneNumber(),
                document.getEmail(),
                document.getBirthDate(),
                document.getAddress() != null ? new Customer.Address(
                        document.getAddress().getDepartment(),
                        document.getAddress().getProvince(),
                        document.getAddress().getDistrict(),
                        document.getAddress().getStreet()) : null,
                document.getOccupation(),
                document.getMonthlyIncome(),
                document.getPep(),
                mapCustomerTypeFromDocument(document.getCustomerType()),
                Customer.KycLevel.valueOf(document.getKycLevel().name()),
                Customer.CustomerStatus.valueOf(document.getStatus().name()),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    public static CustomerDocument.CustomerType mapCustomerTypeToDocument(Customer.CustomerType type) {
        switch (type) {
            case YANKI:
                return CustomerDocument.CustomerType.YAPE;
            case BANKX:
                return CustomerDocument.CustomerType.BCP;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }

    public static Customer.CustomerType mapCustomerTypeFromDocument(CustomerDocument.CustomerType type) {
        switch (type) {
            case YAPE:
                return Customer.CustomerType.YANKI;
            case BCP:
                return Customer.CustomerType.BANKX;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}
