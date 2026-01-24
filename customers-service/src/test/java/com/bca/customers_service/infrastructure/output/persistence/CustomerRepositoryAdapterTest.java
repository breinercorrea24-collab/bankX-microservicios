package com.bca.customers_service.infrastructure.output.persistence;

import com.bca.customers_service.domain.model.Customer;
import com.bca.customers_service.infrastructure.output.persistence.entity.CustomerDocument;
import com.bca.customers_service.infrastructure.output.persistence.repository.CustomerMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryAdapterTest {

    private CustomerRepositoryAdapter adapter;
    private Map<String, CustomerDocument> store;

    @BeforeEach
    void setUp() {
        store = new LinkedHashMap<>();
        CustomerMongoRepository mongoRepository = createProxyRepository();
        adapter = new CustomerRepositoryAdapter(mongoRepository);
    }

    @Test
    void save_persistsAndReturnsDomain() {
        Customer customer = sampleCustomer(null, Customer.CustomerType.BANKX);

        StepVerifier.create(adapter.save(customer))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo("generated-1");
                    assertThat(saved.getCustomerType()).isEqualTo(Customer.CustomerType.BANKX);
                })
                .verifyComplete();

        CustomerDocument persisted = store.values().iterator().next();
        assertThat(persisted.getCustomerType()).isEqualTo(CustomerDocument.CustomerType.BCP);
    }

    @Test
    void findById_returnsExisting() {
        CustomerDocument doc = sampleDocument("c1", CustomerDocument.CustomerType.YAPE);
        store.put(doc.getId(), doc);

        StepVerifier.create(adapter.findById("c1"))
                .assertNext(found -> assertThat(found.getCustomerType()).isEqualTo(Customer.CustomerType.YANKI))
                .verifyComplete();
    }

    @Test
    void findById_emptyWhenMissing() {
        StepVerifier.create(adapter.findById("missing"))
                .verifyComplete();
    }

    @Test
    void existsByDocument_checksStore() {
        CustomerDocument doc = sampleDocument("c2", CustomerDocument.CustomerType.BCP);
        store.put(doc.getId(), doc);

        StepVerifier.create(adapter.existsByDocument("DNI", doc.getDocument().getNumber()))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(adapter.existsByDocument("DNI", "not-found"))
                .expectNext(false)
                .verifyComplete();
    }

    private Customer sampleCustomer(String id, Customer.CustomerType type) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFullName("John Sample");
        customer.setDocument(new Customer.Document(Customer.Document.DocumentType.DNI, "12345678"));
        customer.setPhoneNumber("999888777");
        customer.setEmail("john@example.com");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setAddress(new Customer.Address("Lima", "Lima", "Miraflores", "Av. Example 123"));
        customer.setOccupation("Engineer");
        customer.setMonthlyIncome(5000.0);
        customer.setPep(false);
        customer.setCustomerType(type);
        customer.setKycLevel(Customer.KycLevel.LIGHT);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setCreatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        customer.setUpdatedAt(LocalDateTime.of(2024, 5, 1, 10, 0));
        return customer;
    }

    private CustomerDocument sampleDocument(String id, CustomerDocument.CustomerType type) {
        CustomerDocument.Document doc = new CustomerDocument.Document(
                CustomerDocument.Document.DocumentType.DNI, "12345678");
        CustomerDocument.Address address = new CustomerDocument.Address("Lima", "Lima", "Miraflores", "Av. Example 123");
        return new CustomerDocument(
                id,
                "Persisted",
                doc,
                "999888777",
                "stored@example.com",
                LocalDate.of(1990, 1, 1),
                address,
                "Engineer",
                5000.0,
                false,
                type,
                CustomerDocument.KycLevel.LIGHT,
                CustomerDocument.CustomerStatus.ACTIVE,
                LocalDateTime.of(2024, 5, 1, 10, 0),
                LocalDateTime.of(2024, 5, 1, 10, 0)
        );
    }

    private CustomerMongoRepository createProxyRepository() {
        InvocationHandler handler = new repositoryInvocationHandler();
        return (CustomerMongoRepository) Proxy.newProxyInstance(
                CustomerMongoRepository.class.getClassLoader(),
                new Class[]{CustomerMongoRepository.class},
                handler);
    }

    private class repositoryInvocationHandler implements InvocationHandler {
        private final AtomicLong counter = new AtomicLong(1);

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("save".equals(method.getName()) && args.length == 1) {
                CustomerDocument doc = (CustomerDocument) args[0];
                if (doc.getId() == null) {
                    doc.setId("generated-" + counter.getAndIncrement());
                }
                store.put(doc.getId(), doc);
                return Mono.just(doc);
            }
            if ("findById".equals(method.getName())) {
                String id = (String) args[0];
                return Mono.justOrEmpty(store.get(id));
            }
            if ("existsByDocumentTypeAndDocumentNumber".equals(method.getName())) {
                String type = (String) args[0];
                String number = (String) args[1];
                boolean exists = store.values().stream().anyMatch(doc ->
                        doc.getDocument().getType().name().equals(type) &&
                                doc.getDocument().getNumber().equals(number));
                return Mono.just(exists);
            }
            throw new UnsupportedOperationException("Not needed for tests: " + method.getName());
        }
    }
}
