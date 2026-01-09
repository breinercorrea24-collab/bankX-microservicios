package com.bca.customers_service.infrastructure.output.persistence;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;

    public CustomerRepositoryAdapter(CustomerMongoRepository customerMongoRepository) {
        this.customerMongoRepository = customerMongoRepository;
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        log.info("Saving customer: {}", customer.getFullName());
        CustomerDocument document = mapToDocument(customer);
        return customerMongoRepository.save(document)
                .map(this::mapToCustomer)
                .doOnNext(saved -> log.info("Customer saved with ID: {}", saved.getId()));
    }

    @Override
    public Mono<Customer> findById(String id) {
        log.info("Finding customer by ID: {}", id);
        return customerMongoRepository.findById(id)
                .map(this::mapToCustomer)
                .doOnNext(customer -> log.info("Customer found: {}", id))
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("Customer not found: {}", id)));
    }

    @Override
    public Mono<Boolean> existsByDocument(String documentType, String documentNumber) {
        log.info("Checking if document exists: {}-{}", documentType, documentNumber);
        return customerMongoRepository.existsByDocumentTypeAndDocumentNumber(documentType, documentNumber)
                .doOnNext(exists -> log.info("Document {}-{} exists: {}", documentType, documentNumber, exists));
    }

    private CustomerDocument mapToDocument(Customer customer) {
        return new CustomerDocument(
                customer.getId(),
                customer.getFullName(),
                new CustomerDocument.Document(CustomerDocument.Document.DocumentType.valueOf(customer.getDocument().getType().name()), customer.getDocument().getNumber()),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getBirthDate(),
                customer.getAddress() != null ? new CustomerDocument.Address(
                        customer.getAddress().getDepartment(),
                        customer.getAddress().getProvince(),
                        customer.getAddress().getDistrict(),
                        customer.getAddress().getStreet()
                ) : null,
                customer.getOccupation(),
                customer.getMonthlyIncome(),
                customer.getPep(),
                mapCustomerTypeToDocument(customer.getCustomerType()),
                CustomerDocument.KycLevel.valueOf(customer.getKycLevel().name()),
                CustomerDocument.CustomerStatus.valueOf(customer.getStatus().name()),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private Customer mapToCustomer(CustomerDocument document) {
        return new Customer(
                document.getId(),
                document.getFullName(),
                new Customer.Document(Customer.Document.DocumentType.valueOf(document.getDocument().getType().name()), document.getDocument().getNumber()),
                document.getPhoneNumber(),
                document.getEmail(),
                document.getBirthDate(),
                document.getAddress() != null ? new Customer.Address(
                        document.getAddress().getDepartment(),
                        document.getAddress().getProvince(),
                        document.getAddress().getDistrict(),
                        document.getAddress().getStreet()
                ) : null,
                document.getOccupation(),
                document.getMonthlyIncome(),
                document.getPep(),
                mapCustomerTypeFromDocument(document.getCustomerType()),
                Customer.KycLevel.valueOf(document.getKycLevel().name()),
                Customer.CustomerStatus.valueOf(document.getStatus().name()),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    private CustomerDocument.CustomerType mapCustomerTypeToDocument(Customer.CustomerType type) {
        switch (type) {
            case YANKI:
                return CustomerDocument.CustomerType.YAPE;
            case BANKX:
                return CustomerDocument.CustomerType.BCP;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }

    private Customer.CustomerType mapCustomerTypeFromDocument(CustomerDocument.CustomerType type) {
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
