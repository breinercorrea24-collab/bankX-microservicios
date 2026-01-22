package com.bca.customers_service.application;

import com.bca.customers_service.domain.dto.CustomerCreateRequest;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCustomerUseCaseImplTest {

    private CreateCustomerUseCaseImpl useCase;
    private StubCustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new StubCustomerRepository();
        useCase = new CreateCustomerUseCaseImpl(customerRepository);
    }

    @Test
    void shouldCreateCustomerWhenDocumentDoesNotExist() {
        CustomerCreateRequest request = buildRequest();
        customerRepository.existsByDocumentResult = Mono.just(false);

        StepVerifier.create(useCase.execute(request))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo("generated-id");
                    assertThat(saved.getFullName()).isEqualTo(request.getFullName());
                    assertThat(saved.getDocument().getType().name()).isEqualTo(request.getDocument().getType().name());
                    assertThat(saved.getDocument().getNumber()).isEqualTo(request.getDocument().getNumber());
                    assertThat(saved.getAddress()).isNotNull();
                    assertThat(saved.getCustomerType()).isEqualTo(Customer.CustomerType.BANKX);
                    assertThat(saved.getKycLevel()).isEqualTo(Customer.KycLevel.LIGHT);
                    assertThat(saved.getStatus()).isEqualTo(Customer.CustomerStatus.ACTIVE);
                })
                .verifyComplete();

        assertThat(customerRepository.savedCustomer).isNotNull();
        assertThat(customerRepository.savedCustomer.getCreatedAt()).isNotNull();
        assertThat(customerRepository.savedCustomer.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldErrorWhenDocumentAlreadyExists() {
        CustomerCreateRequest request = buildRequest();
        customerRepository.existsByDocumentResult = Mono.just(true);

        StepVerifier.create(useCase.execute(request))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Document already exists"))
                .verify();

        assertThat(customerRepository.savedCustomer).isNull();
    }

    private CustomerCreateRequest buildRequest() {
        CustomerCreateRequest.Document document = new CustomerCreateRequest.Document(
                CustomerCreateRequest.Document.DocumentType.DNI, "12345678"
        );

        CustomerCreateRequest.Address address = new CustomerCreateRequest.Address(
                "Lima", "Lima", "Miraflores", "Av. Example 123"
        );

        return new CustomerCreateRequest(
                CustomerCreateRequest.CustomerType.BANKX,
                "John Doe",
                document,
                "+51999999999",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                address,
                "Engineer",
                5000.0,
                false
        );
    }

    private static class StubCustomerRepository implements CustomerRepository {
        Mono<Boolean> existsByDocumentResult;
        Customer savedCustomer;

        @Override
        public Mono<Customer> save(Customer customer) {
            customer.setId("generated-id");
            this.savedCustomer = customer;
            return Mono.just(customer);
        }

        @Override
        public Mono<Customer> findById(String id) {
            return Mono.empty();
        }

        @Override
        public Mono<Boolean> existsByDocument(String documentType, String documentNumber) {
            return existsByDocumentResult != null ? existsByDocumentResult : Mono.just(false);
        }
    }
}
