package com.bca.customers_service.application;

import com.bca.customers_service.domain.dto.CustomerResponse;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GetCustomerUseCaseImplTest {

    private GetCustomerUseCaseImpl useCase;
    private StubCustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new StubCustomerRepository();
        useCase = new GetCustomerUseCaseImpl(customerRepository);
    }

    @Test
    void shouldReturnCustomerResponseWhenFound() {
        Customer customer = buildCustomer();
        customerRepository.store(customer);

        StepVerifier.create(useCase.execute("abc"))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("abc");
                    assertThat(response.getFullName()).isEqualTo(customer.getFullName());
                    assertThat(response.getDocument()).isEqualTo("DNI-12345678");
                    assertThat(response.getCustomerType()).isEqualTo(CustomerResponse.CustomerType.BANKX);
                    assertThat(response.getKycLevel()).isEqualTo(CustomerResponse.KycLevel.LIGHT);
                    assertThat(response.getStatus()).isEqualTo(CustomerResponse.CustomerStatus.ACTIVE);
                    assertThat(response.getCreatedAt()).isEqualTo(customer.getCreatedAt());
                })
                .verifyComplete();
    }

    @Test
    void shouldMapYankiCustomerType() {
        Customer customer = buildCustomer();
        customer.setCustomerType(Customer.CustomerType.YANKI);
        customerRepository.store(customer);

        StepVerifier.create(useCase.execute("abc"))
                .assertNext(response -> assertThat(response.getCustomerType())
                        .isEqualTo(CustomerResponse.CustomerType.YANKI))
                .verifyComplete();
    }

    @Test
    void shouldErrorWhenCustomerNotFound() {
        StepVerifier.create(useCase.execute("missing"))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Customer not found"))
                .verify();
    }

    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setId("abc");
        customer.setFullName("Jane Roe");
        customer.setDocument(new Customer.Document(
                Customer.Document.DocumentType.DNI, "12345678"
        ));
        customer.setCustomerType(Customer.CustomerType.BANKX);
        customer.setKycLevel(Customer.KycLevel.LIGHT);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setBirthDate(LocalDate.of(1992, 2, 2));
        customer.setCreatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        customer.setUpdatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        return customer;
    }

    private static class StubCustomerRepository implements CustomerRepository {
        private final Map<String, Customer> data = new HashMap<>();

        @Override
        public Mono<Customer> save(Customer customer) {
            data.put(customer.getId(), customer);
            return Mono.just(customer);
        }

        @Override
        public Mono<Customer> findById(String id) {
            return Mono.justOrEmpty(data.get(id));
        }

        @Override
        public Mono<Boolean> existsByDocument(String documentType, String documentNumber) {
            return Mono.just(false);
        }

        void store(Customer customer) {
            data.put(customer.getId(), customer);
        }
    }
}
