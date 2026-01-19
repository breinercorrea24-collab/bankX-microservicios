package com.bca.core_banking_service.application.usecases.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.domain.ports.output.rest.ExternalCardsClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ValidationProductTest {

    @Mock
    private ExternalCardsClient externalCardsClient;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CreditRepository creditRepository;

    private ValidationProduct validationProduct;

    @BeforeEach
    void setUp() {
        validationProduct = new ValidationProduct(externalCardsClient, accountRepository, creditRepository);
    }

    @Test
    void validateAccountCreation_emitsErrorWhenOverdueCredits() {
        when(creditRepository.hasOverdueCredits("cust")).thenReturn(Mono.just(true));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .expectError(BusinessException.class)
                .verify();

        verify(accountRepository, never()).findByCustomerId(any());
        verify(externalCardsClient, never()).hasCreditCard(any());
    }

    @Test
    void validateAccountCreation_personalCustomerWithoutExistingAccount_succeeds() {
        when(creditRepository.hasOverdueCredits("cust")).thenReturn(Mono.just(false));
        when(accountRepository.findByCustomerId("cust")).thenReturn(Flux.empty());

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .verifyComplete();
    }

    @Test
    void validateAccountCreation_businessCustomerBlockingTypes_emitBusinessException() {
        when(creditRepository.hasOverdueCredits("cust")).thenReturn(Mono.just(false));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.SAVINGS,
                CustomerType.BUSINESS))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validateAccountCreation_vipPersonalRequiresCreditCard() {
        when(creditRepository.hasOverdueCredits("vip")).thenReturn(Mono.just(false));
        when(externalCardsClient.hasCreditCard("vip")).thenReturn(Mono.just(ResponseEntity.ok(true)));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "vip",
                AccountType.VIP_SAVINGS,
                CustomerType.VIPPERSONAL))
                .verifyComplete();

        when(externalCardsClient.hasCreditCard("vip")).thenReturn(Mono.just(ResponseEntity.ok(false)));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "vip",
                AccountType.VIP_SAVINGS,
                CustomerType.VIPPERSONAL))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validateAccountCreation_pymeBusinessRequiresCreditCard() {
        when(creditRepository.hasOverdueCredits("pyme")).thenReturn(Mono.just(false));
        when(externalCardsClient.hasCreditCard("pyme")).thenReturn(Mono.just(ResponseEntity.ok(true)));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "pyme",
                AccountType.PYME_CHECKING,
                CustomerType.PYMEBUSINESS))
                .verifyComplete();

        when(externalCardsClient.hasCreditCard("pyme")).thenReturn(Mono.just(ResponseEntity.ok(false)));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "pyme",
                AccountType.PYME_CHECKING,
                CustomerType.PYMEBUSINESS))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void hasOverdueCredits_delegatesToRepositoryAndLogs() {
        when(creditRepository.hasOverdueCredits("cust")).thenReturn(Mono.just(false));

        StepVerifier.create(validationProduct.hasOverdueCredits("cust"))
                .expectNext(false)
                .verifyComplete();

        verify(creditRepository).hasOverdueCredits("cust");
    }

    @Test
    void validateAccountCreation_personalWithExistingSameType_emitsBusinessException() {
        SavingsAccount existing = new SavingsAccount(
                "cust",
                "USD",
                com.bca.core_banking_service.domain.model.enums.product.ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                1,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
        existing.setType(AccountType.SAVINGS);

        when(creditRepository.hasOverdueCredits("cust")).thenReturn(Mono.just(false));
        when(accountRepository.findByCustomerId("cust")).thenReturn(Flux.just(existing));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void applyCustomerRules_dispatchesToPersonalValidator() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);
        when(validator.validatePersonalCustomer("cust", AccountType.SAVINGS, CustomerType.PERSONAL))
                .thenReturn(Mono.empty());

        invokeApplyCustomerRules(validator, "cust", AccountType.SAVINGS, CustomerType.PERSONAL)
                .verifyComplete();

        verify(validator).validatePersonalCustomer("cust", AccountType.SAVINGS, CustomerType.PERSONAL);
    }

    @Test
    void applyCustomerRules_dispatchesToBusinessValidator() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);
        when(validator.validateBusinessCustomer("cust", AccountType.CHECKING, CustomerType.BUSINESS))
                .thenReturn(Mono.empty());

        invokeApplyCustomerRules(validator, "cust", AccountType.CHECKING, CustomerType.BUSINESS)
                .verifyComplete();

        verify(validator).validateBusinessCustomer("cust", AccountType.CHECKING, CustomerType.BUSINESS);
    }

    @Test
    void applyCustomerRules_dispatchesToVipPersonalValidator() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);
        when(validator.validateVipPersonalCustomer("cust", AccountType.VIP_SAVINGS, CustomerType.VIPPERSONAL))
                .thenReturn(Mono.empty());

        invokeApplyCustomerRules(validator, "cust", AccountType.VIP_SAVINGS, CustomerType.VIPPERSONAL)
                .verifyComplete();

        verify(validator).validateVipPersonalCustomer("cust", AccountType.VIP_SAVINGS, CustomerType.VIPPERSONAL);
    }

    @Test
    void applyCustomerRules_dispatchesToPymeBusinessValidator() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);
        when(validator.validatePymeBusinessCustomer("cust", AccountType.PYME_CHECKING, CustomerType.PYMEBUSINESS))
                .thenReturn(Mono.empty());

        invokeApplyCustomerRules(validator, "cust", AccountType.PYME_CHECKING, CustomerType.PYMEBUSINESS)
                .verifyComplete();

        verify(validator).validatePymeBusinessCustomer("cust", AccountType.PYME_CHECKING, CustomerType.PYMEBUSINESS);
    }

    private StepVerifier.FirstStep<Void> invokeApplyCustomerRules(
            ValidationCustomer validator,
            String customerId,
            AccountType type,
            CustomerType customerType) throws Exception {

        Method method = ValidationProduct.class.getDeclaredMethod(
                "applyCustomerRules",
                ValidationCustomer.class,
                String.class,
                AccountType.class,
                CustomerType.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Mono<Void> result = (Mono<Void>) method.invoke(validationProduct, validator, customerId, type, customerType);
        return StepVerifier.create(result);
    }

    @Test
    void applyCustomerRules_defaultBranchReturnsEmptyWhenCustomerTypeUnknown() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);

        invokeApplyCustomerRules(validator, "cust", AccountType.SAVINGS, null)
                .verifyComplete();

        Mockito.verifyNoInteractions(validator);
    }

    @Test
    void applyCustomerRules_dispatchesSwitchForPersonal() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);
        when(validator.validatePersonalCustomer("cust", AccountType.SAVINGS, CustomerType.PERSONAL))
                .thenReturn(Mono.empty());

        invokeApplyCustomerRules(validator, "cust", AccountType.SAVINGS, CustomerType.PERSONAL)
                .verifyComplete();

        verify(validator).validatePersonalCustomer("cust", AccountType.SAVINGS, CustomerType.PERSONAL);
    }

    @Test
    void applyCustomerRules_defaultBranchForUnknownCustomerType() throws Exception {
        ValidationCustomer validator = Mockito.mock(ValidationCustomer.class);

        invokeApplyCustomerRules(validator, "cust", AccountType.SAVINGS, null)
                .verifyComplete();

        Mockito.verifyNoInteractions(validator);
    }

    @Test
    void validateByCustomerType_dispatchesPersonal() throws Exception {
        when(accountRepository.findByCustomerId("cust")).thenReturn(Flux.empty());

        invokeValidateByCustomerType("cust", AccountType.SAVINGS, CustomerType.PERSONAL)
                .verifyComplete();

        verify(accountRepository).findByCustomerId("cust");
    }

    @Test
    void validateByCustomerType_dispatchesBusiness() throws Exception {
        invokeValidateByCustomerType("cust", AccountType.CHECKING, CustomerType.BUSINESS)
                .verifyComplete();
    }

    @Test
    void validateByCustomerType_dispatchesVipPersonal() throws Exception {
        when(externalCardsClient.hasCreditCard("vip")).thenReturn(Mono.just(ResponseEntity.ok(true)));

        invokeValidateByCustomerType("vip", AccountType.VIP_SAVINGS, CustomerType.VIPPERSONAL)
                .verifyComplete();

        verify(externalCardsClient).hasCreditCard("vip");
    }

    @Test
    void validateByCustomerType_dispatchesPymeBusiness() throws Exception {
        when(externalCardsClient.hasCreditCard("pyme")).thenReturn(Mono.just(ResponseEntity.ok(true)));

        invokeValidateByCustomerType("pyme", AccountType.PYME_CHECKING, CustomerType.PYMEBUSINESS)
                .verifyComplete();

        verify(externalCardsClient).hasCreditCard("pyme");
    }

    @Test
    void validateByCustomerType_returnsEmptyForUnknown() throws Exception {
        invokeValidateByCustomerType("cust", AccountType.SAVINGS, null)
                .verifyComplete();
    }

    private StepVerifier.FirstStep<Void> invokeValidateByCustomerType(
            String customerId,
            AccountType type,
            CustomerType customerType) throws Exception {
        Method method = ValidationProduct.class.getDeclaredMethod(
                "validateByCustomerType",
                String.class,
                AccountType.class,
                CustomerType.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Mono<Void> result = (Mono<Void>) method.invoke(validationProduct, customerId, type, customerType);
        return StepVerifier.create(result);
    }
}
