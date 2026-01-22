package com.bca.customers_service.infrastructure.input.rest;

import com.bca.customers_service.application.CreateCustomerUseCaseImpl;
import com.bca.customers_service.application.GetCustomerSummaryUseCaseImpl;
import com.bca.customers_service.application.GetCustomerUseCaseImpl;
import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.domain.ports.AccountService;
import com.bca.customers_service.domain.ports.CreditService;
import com.bca.customers_service.domain.ports.output.CustomerRepository;
import com.bca.customers_service.dto.CustomerCreate;
import com.bca.customers_service.dto.CustomerCreateDocument;
import com.bca.customers_service.dto.CustomerResponse;
import com.bca.customers_service.dto.CustomerSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CustomersApiDelegateImplTest {

    private StubCustomerRepository customerRepository;
    private StubAccountService accountService;
    private StubCreditService creditService;
    private CustomersApiDelegateImpl delegate;
    private ServerWebExchange exchange = null; // not used in implementation

    @BeforeEach
    void setUp() {
        customerRepository = new StubCustomerRepository();
        accountService = new StubAccountService();
        creditService = new StubCreditService();

        CreateCustomerUseCaseImpl createCustomerUseCase = new CreateCustomerUseCaseImpl(customerRepository);
        GetCustomerUseCaseImpl getCustomerUseCase = new GetCustomerUseCaseImpl(customerRepository);
        GetCustomerSummaryUseCaseImpl getCustomerSummaryUseCase = new GetCustomerSummaryUseCaseImpl(accountService, creditService);

        delegate = new CustomersApiDelegateImpl(createCustomerUseCase, getCustomerUseCase, getCustomerSummaryUseCase);
    }

    @Test
    void customersCustomerIdGet_returnsOkWhenFound() {
        Customer stored = sampleCustomer("cust-1");
        customerRepository.store(stored);

        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersCustomerIdGet("cust-1", exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    CustomerResponse body = response.getBody();
                    assertThat(body.getId()).isEqualTo("cust-1");
                    assertThat(body.getFullName()).isEqualTo(stored.getFullName());
                    assertThat(body.getDocument()).isEqualTo("DNI-12345678");
                    assertThat(body.getCustomerType()).isEqualTo(CustomerResponse.CustomerTypeEnum.BANKX);
                    assertThat(body.getCreatedAt()).isEqualTo(OffsetDateTime.ofInstant(stored.getCreatedAt().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
                })
                .verifyComplete();
    }

    @Test
    void customersCustomerIdGet_returnsNotFoundWhenMissing() {
        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersCustomerIdGet("missing", exchange);

        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND))
                .verifyComplete();
    }

    @Test
    void customersCustomerIdGet_returnsServerErrorOnUnexpected() {
        customerRepository.findError = new RuntimeException("db down");

        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersCustomerIdGet("any", exchange);

        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void customersCustomerIdSummaryGet_returnsOk() {
        accountService.accounts = List.of(
                new com.bca.customers_service.domain.dto.AccountSummary("a1",
                        com.bca.customers_service.domain.dto.AccountSummary.AccountType.SAVINGS,
                        "PEN", 100.0, 90.0,
                        com.bca.customers_service.domain.dto.AccountSummary.AccountStatus.ACTIVE)
        );
        creditService.credits = List.of(
                new com.bca.customers_service.domain.dto.CreditSummary("c1", 500.0, 200.0, "USD",
                        com.bca.customers_service.domain.dto.CreditSummary.CreditStatus.ACTIVE)
        );

        Mono<ResponseEntity<CustomerSummaryResponse>> result = delegate.customersCustomerIdSummaryGet("cust-1", exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getCustomerId()).isEqualTo("cust-1");
                    assertThat(response.getBody().getTotalAccounts()).isEqualTo(1);
                    assertThat(response.getBody().getTotalBalance()).isEqualTo(100.0);
                    assertThat(response.getBody().getAccounts()).hasSize(1);
                    assertThat(response.getBody().getCredits()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void customersCustomerIdSummaryGet_propagatesErrors() {
        accountService.error = new RuntimeException("accounts failure");

        Mono<ResponseEntity<CustomerSummaryResponse>> result = delegate.customersCustomerIdSummaryGet("cust-err", exchange);

        StepVerifier.create(result)
                .expectErrorMatches(err -> err instanceof RuntimeException && err.getMessage().equals("accounts failure"))
                .verify();
    }

    @Test
    void customersPost_createsCustomer() {
        customerRepository.existsResult = Mono.just(false);
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.DNI)
                .number("12345678");
        CustomerCreate createDto = new CustomerCreate(CustomerCreate.CustomerTypeEnum.BANKX, "John Doe", doc, "999888777")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1));

        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersPost(Mono.just(createDto), exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.getBody()).isNotNull();
                    CustomerResponse body = response.getBody();
                    assertThat(body.getId()).isEqualTo("generated-id");
                    assertThat(body.getFullName()).isEqualTo("John Doe");
                    assertThat(body.getCustomerType()).isEqualTo(CustomerResponse.CustomerTypeEnum.BANKX);
                })
                .verifyComplete();

        assertThat(customerRepository.savedCustomer).isNotNull();
    }

    @Test
    void customersPost_returnsBadRequestWhenDocumentExists() {
        customerRepository.existsResult = Mono.just(true);
        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.DNI)
                .number("12345678");
        CustomerCreate createDto = new CustomerCreate(CustomerCreate.CustomerTypeEnum.BANKX, "John Doe", doc, "999888777");

        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersPost(Mono.just(createDto), exchange);

        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void customersPost_returnsServerErrorOnUnexpected() {
        customerRepository.existsResult = Mono.just(false);
        customerRepository.saveError = new RuntimeException("save failed");

        CustomerCreateDocument doc = new CustomerCreateDocument()
                .type(CustomerCreateDocument.TypeEnum.DNI)
                .number("12345678");
        CustomerCreate createDto = new CustomerCreate(CustomerCreate.CustomerTypeEnum.BANKX, "John Doe", doc, "999888777");

        Mono<ResponseEntity<CustomerResponse>> result = delegate.customersPost(Mono.just(createDto), exchange);

        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    private Customer sampleCustomer(String id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFullName("John Test");
        customer.setDocument(new Customer.Document(Customer.Document.DocumentType.DNI, "12345678"));
        customer.setCustomerType(Customer.CustomerType.BANKX);
        customer.setKycLevel(Customer.KycLevel.LIGHT);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setCreatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        customer.setUpdatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        return customer;
    }

    // ===== Stub implementations =====

    private static class StubCustomerRepository implements CustomerRepository {
        Mono<Boolean> existsResult = Mono.just(false);
        RuntimeException findError;
        RuntimeException saveError;
        Customer savedCustomer;
        List<Customer> data = new ArrayList<>();

        @Override
        public Mono<Customer> save(Customer customer) {
            if (saveError != null) return Mono.error(saveError);
            if (customer.getId() == null) {
                customer.setId("generated-id");
            }
            this.savedCustomer = customer;
            data.add(customer);
            return Mono.just(customer);
        }

        @Override
        public Mono<Customer> findById(String id) {
            if (findError != null) return Mono.error(findError);
            return data.stream().filter(c -> id.equals(c.getId()))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty());
        }

        @Override
        public Mono<Boolean> existsByDocument(String documentType, String documentNumber) {
            return existsResult;
        }

        void store(Customer customer) {
            data.add(customer);
        }
    }

    private static class StubAccountService implements AccountService {
        List<com.bca.customers_service.domain.dto.AccountSummary> accounts = new ArrayList<>();
        RuntimeException error;

        @Override
        public Flux<com.bca.customers_service.domain.dto.AccountSummary> getAccountsByCustomerId(String customerId) {
            if (error != null) return Flux.error(error);
            return Flux.fromIterable(accounts);
        }
    }

    private static class StubCreditService implements CreditService {
        List<com.bca.customers_service.domain.dto.CreditSummary> credits = new ArrayList<>();

        @Override
        public Flux<com.bca.customers_service.domain.dto.CreditSummary> getCreditsByCustomerId(String customerId) {
            return Flux.fromIterable(credits);
        }
    }
}
