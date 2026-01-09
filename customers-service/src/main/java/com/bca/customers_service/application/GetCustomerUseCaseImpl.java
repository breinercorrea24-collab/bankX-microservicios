package com.bca.customers_service.application;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.input.GetCustomerUseCase;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import com.bca.customers_service.domain.dto.CustomerResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GetCustomerUseCaseImpl implements GetCustomerUseCase{

    private static final Logger logger = LoggerFactory.getLogger(GetCustomerUseCaseImpl.class);

    private final CustomerRepository customerRepository;

    public GetCustomerUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<CustomerResponse> execute(String customerId) {
        logger.info("Retrieving customer with ID: {}", customerId);
        return customerRepository.findById(customerId)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")))
                .doOnNext(response -> logger.info("Customer retrieved successfully: {}", customerId))
                .doOnError(error -> logger.error("Error retrieving customer {}: {}", customerId, error.getMessage()));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse.CustomerType customerType = mapCustomerType(customer.getCustomerType());
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getDocument().getType() + "-" + customer.getDocument().getNumber(),
                customerType,
                CustomerResponse.KycLevel.valueOf(customer.getKycLevel().name()),
                CustomerResponse.CustomerStatus.valueOf(customer.getStatus().name()),
                customer.getCreatedAt()
        );
    }

    private CustomerResponse.CustomerType mapCustomerType(Customer.CustomerType type) {
        switch (type) {
            case YANKI:
                return CustomerResponse.CustomerType.YANKI;
            case BANKX:
                return CustomerResponse.CustomerType.BANKX;
            default:
                throw new IllegalArgumentException("Unknown customer type: " + type);
        }
    }
}