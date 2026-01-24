package com.bca.customers_service.application;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.input.CreateCustomerUseCase;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import com.bca.customers_service.domain.dto.CustomerCreateRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateCustomerUseCaseImpl.class);

    private static final Map<CustomerCreateRequest.CustomerType, Customer.CustomerType> CUSTOMER_TYPE_MAP =
            Map.of(
                    CustomerCreateRequest.CustomerType.YANKI, Customer.CustomerType.YANKI,
                    CustomerCreateRequest.CustomerType.BANKX, Customer.CustomerType.BANKX
            );

    private final CustomerRepository customerRepository;

    public CreateCustomerUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> execute(CustomerCreateRequest request) {
        logger.info("Starting customer creation for document: {}", request.getDocument().getNumber());
        return customerRepository.existsByDocument(
                request.getDocument().getType().name(),
                request.getDocument().getNumber()
        )
        .flatMap(exists -> {
            if (exists) {
                logger.warn("Document already exists: {}", request.getDocument().getNumber());
                return Mono.error(new RuntimeException("Document already exists"));
            }

            Customer customer = new Customer();
            customer.setFullName(request.getFullName());
            customer.setDocument(new Customer.Document(
                Customer.Document.DocumentType.valueOf(request.getDocument().getType().name()),
                request.getDocument().getNumber()
            ));
            customer.setPhoneNumber(request.getPhoneNumber());
            customer.setEmail(request.getEmail());
            customer.setBirthDate(request.getBirthDate());
            customer.setAddress(request.getAddress() != null ? new Customer.Address(
                request.getAddress().getDepartment(),
                request.getAddress().getProvince(),
                request.getAddress().getDistrict(),
                request.getAddress().getStreet()
            ) : null);
            customer.setOccupation(request.getOccupation());
            customer.setMonthlyIncome(request.getMonthlyIncome());
            customer.setPep(request.getPep());
            customer.setCustomerType(mapCustomerType(request.getCustomerType()));
            customer.setKycLevel(Customer.KycLevel.LIGHT); // default
            customer.setStatus(Customer.CustomerStatus.ACTIVE); // default
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());

            logger.info("Saving new customer: {}", customer.getFullName());
            return customerRepository.save(customer);
        })
        .doOnNext(customer -> logger.info("Customer created successfully with ID: {}", customer.getId()))
        .doOnError(error -> logger.error("Error creating customer: {}", error.getMessage()));
    }

    private Customer.CustomerType mapCustomerType(CustomerCreateRequest.CustomerType type) {
        if (type == null) {
            throw new IllegalArgumentException("Unknown customer type: " + type);
        }

        Customer.CustomerType customerType = CUSTOMER_TYPE_MAP.get(type);
        if (customerType == null) {
            throw new IllegalArgumentException("Unknown customer type: " + type);
        }
        return customerType;
    }
}
