package com.bca.customers_service.infrastructure.output.persistence;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import com.bca.customers_service.infrastructure.output.persistence.entity.CustomerDocument;
import com.bca.customers_service.infrastructure.output.persistence.mapper.CustomerMapper;
import com.bca.customers_service.infrastructure.output.persistence.repository.CustomerMongoRepository;

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
        CustomerDocument document = CustomerMapper.mapToDocument(customer);
        return customerMongoRepository.save(document)
                .map(CustomerMapper::mapToCustomer)
                .doOnNext(saved -> log.info("Customer saved with ID: {}", saved.getId()));
    }

    @Override
    public Mono<Customer> findById(String id) {
        log.info("Finding customer by ID: {}", id);
        return customerMongoRepository.findById(id)
                .map(CustomerMapper::mapToCustomer)
                .doOnNext(customer -> log.info("Customer found: {}", id))
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("Customer not found: {}", id)));
    }

    @Override
    public Mono<Boolean> existsByDocument(String documentType, String documentNumber) {
        log.info("Checking if document exists: {}-{}", documentType, documentNumber);
        return customerMongoRepository.existsByDocumentTypeAndDocumentNumber(documentType, documentNumber)
                .doOnNext(exists -> log.info("Document {}-{} exists: {}", documentType, documentNumber, exists));
    }

    
}
