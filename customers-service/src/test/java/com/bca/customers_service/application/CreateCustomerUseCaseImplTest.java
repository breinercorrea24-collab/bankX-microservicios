package com.bca.customers_service.application;

import com.bca.customers_service.domain.dto.CustomerCreateRequest;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCustomerUseCaseImplTest {

    private CreateCustomerUseCaseImpl useCase;
    private StubCustomerRepository customerRepository;
    private static final Method MAP_CUSTOMER_TYPE_METHOD;

    static {
        try {
            MAP_CUSTOMER_TYPE_METHOD = CreateCustomerUseCaseImpl.class
                    .getDeclaredMethod("mapCustomerType", CustomerCreateRequest.CustomerType.class);
            MAP_CUSTOMER_TYPE_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        customerRepository = new StubCustomerRepository();
        useCase = new CreateCustomerUseCaseImpl(customerRepository);
    }

    @Test
    void shouldCreateCustomerWhenDocumentDoesNotExist() {
        CustomerCreateRequest request = buildRequest(CustomerCreateRequest.CustomerType.BANKX, true);
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
        CustomerCreateRequest request = buildRequest(CustomerCreateRequest.CustomerType.BANKX, true);
        customerRepository.existsByDocumentResult = Mono.just(true);

        StepVerifier.create(useCase.execute(request))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Document already exists"))
                .verify();

        assertThat(customerRepository.savedCustomer).isNull();
    }

    private CustomerCreateRequest buildRequest() {
        return buildRequest(CustomerCreateRequest.CustomerType.BANKX, true);
    }

    private CustomerCreateRequest buildRequest(CustomerCreateRequest.CustomerType type, boolean includeAddress) {
        CustomerCreateRequest.Document document = new CustomerCreateRequest.Document(
                CustomerCreateRequest.Document.DocumentType.DNI, "12345678"
        );

        CustomerCreateRequest.Address address = new CustomerCreateRequest.Address(
                "Lima", "Lima", "Miraflores", "Av. Example 123"
        );

        return new CustomerCreateRequest(
                type,
                "John Doe",
                document,
                "+51999999999",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                includeAddress ? address : null,
                "Engineer",
                5000.0,
                false
        );
    }

    @Test
    void shouldMapYankiCustomerType() {
        assertThat(invokeMapCustomerType(CustomerCreateRequest.CustomerType.YANKI))
                .isEqualTo(Customer.CustomerType.YANKI);
    }

    @Test
    void shouldMapBankxCustomerType() {
        assertThat(invokeMapCustomerType(CustomerCreateRequest.CustomerType.BANKX))
                .isEqualTo(Customer.CustomerType.BANKX);
    }

    @Test
    void shouldRejectUnknownCustomerType() {
        assertThatThrownBy(() -> invokeMapCustomerType(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown customer type: null");
    }

    private Customer.CustomerType invokeMapCustomerType(CustomerCreateRequest.CustomerType type) {
        try {
            return (Customer.CustomerType) MAP_CUSTOMER_TYPE_METHOD.invoke(useCase, type);
        } catch (ReflectiveOperationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause != null) {
                throw new RuntimeException(cause);
            }
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateYankiCustomerWithNullAddress() {
        CustomerCreateRequest request = buildRequest(CustomerCreateRequest.CustomerType.YANKI, false);
        customerRepository.existsByDocumentResult = Mono.just(false);

        StepVerifier.create(useCase.execute(request))
                .assertNext(saved -> {
                    assertThat(saved.getCustomerType()).isEqualTo(Customer.CustomerType.YANKI);
                    assertThat(saved.getAddress()).isNull();
                })
                .verifyComplete();
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
