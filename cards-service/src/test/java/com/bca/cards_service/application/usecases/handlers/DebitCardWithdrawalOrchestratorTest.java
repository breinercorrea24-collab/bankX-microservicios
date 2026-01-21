package com.bca.cards_service.application.usecases.handlers;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.rest.ExternalAccountsClient;
import com.bca.cards_service.infrastructure.output.rest.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebitCardWithdrawalOrchestratorTest {

    private StubAccountsClient accountsClient;
    private DebitCardWithdrawalOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        accountsClient = new StubAccountsClient();
        orchestrator = new DebitCardWithdrawalOrchestrator(accountsClient);
    }

    @Test
    void withdrawsAcrossAccountsUntilAmountCovered() {
        DebitCard card = new DebitCard("card-1", "4111", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-1", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(List.of("acc-2")));
        accountsClient.balances.put("acc-1", balance("acc-1", "50"));
        accountsClient.balances.put("acc-2", balance("acc-2", "60"));

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("70")))
                .verifyComplete();

        assertEquals(new BigDecimal("50"), accountsClient.withdrawals.get("acc-1"));
        assertEquals(new BigDecimal("20"), accountsClient.withdrawals.get("acc-2"));
    }

    @Test
    void failsWhenBalanceInsufficient() {
        DebitCard card = new DebitCard("card-2", "4222", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-2", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(List.of("acc-2")));
        accountsClient.balances.put("acc-1", balance("acc-1", "0"));
        accountsClient.balances.put("acc-2", balance("acc-2", "5"));

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("10")))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void skipsWithdrawalWhenBalanceIsNullOrZero() {
        DebitCard card = new DebitCard("card-3", "4333", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-3", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(Set.of()));
        accountsClient.balances.put("acc-1", balanceWithNullBalance("acc-1"));

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("5")))
                .expectError(BusinessException.class)
                .verify();

        assertTrue(accountsClient.withdrawals.isEmpty());
    }

    @Test
    void handlesNullBalanceResponse() {
        ExternalAccountsClient nullBalanceClient = new ExternalAccountsClient() {
            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountWithdrawal(String accountId, BigDecimal amount) {
                return Mono.just(ResponseEntity.ok(new AccountBalanceResponse()));
            }

            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountBalance(String accountId) {
                return Mono.justOrEmpty((ResponseEntity<AccountBalanceResponse>) null);
            }
        };

        DebitCardWithdrawalOrchestrator orchestratorNullBalance = new DebitCardWithdrawalOrchestrator(
                nullBalanceClient);
        DebitCard card = new DebitCard("card-4", "4444", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-4", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(Set.of()));

        StepVerifier.create(orchestratorNullBalance.withdrawCascade(card, new BigDecimal("10")))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void skipsWhenRemainingIsZeroAfterFirstAccount() {
        ExternalAccountsClient mixedClient = new ExternalAccountsClient() {
            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountWithdrawal(String accountId, BigDecimal amount) {
                return Mono.just(ResponseEntity.ok(new AccountBalanceResponse()));
            }

            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountBalance(String accountId) {
                if (accountId.equals("acc-1")) {
                    AccountBalanceResponse acc = new AccountBalanceResponse();
                    acc.setBalance(new BigDecimal("10"));
                    return Mono.just(ResponseEntity.ok(acc));
                }
                // second call returns body with null balance to hit getSafeBalance null branch
                AccountBalanceResponse acc = new AccountBalanceResponse();
                acc.setBalance(null);
                return Mono.just(ResponseEntity.ok(acc));
            }
        };

        DebitCardWithdrawalOrchestrator orchestrator = new DebitCardWithdrawalOrchestrator(mixedClient);
        DebitCard card = new DebitCard("card-5", "4555", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-5", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(List.of("acc-2")));

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("5")))
                .verifyComplete();
    }
    @Test
    @DisplayName("Cobertura Línea 59: Cuando el objeto account es nulo")
    void getSafeBalance_WhenAccountIsNull_ReturnsZero() {
        try {
            var method = DebitCardWithdrawalOrchestrator.class
                    .getDeclaredMethod("getSafeBalance", AccountBalanceResponse.class);
            method.setAccessible(true);

            BigDecimal result = (BigDecimal) method.invoke(orchestrator, new Object[] { null });
            assertEquals(BigDecimal.ZERO, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Cobertura Línea 59: Cuando balance es nulo dentro de account")
    void getSafeBalance_WhenBalanceIsNull_ReturnsZero() {
        DebitCard card = createBaseCard("acc-1");

        // Simulamos respuesta con body pero con campo balance nulo
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setId("acc-1");
        response.setBalance(null);
        accountsClient.balances.put("acc-1", response);

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("10")))
                .expectError(BusinessException.class)
                .verify();

        // Internamente getSafeBalance(response) detectó balance null y devolvió ZERO
    }

    @Test
    @DisplayName("Cobertura Línea 40: Cuando el ResponseEntity es nulo")
    void withdrawFromAccount_WhenResponseIsNull_HitsTernary() {
        ExternalAccountsClient mockClient = org.mockito.Mockito.mock(ExternalAccountsClient.class);
        org.mockito.Mockito.when(mockClient.AccountBalance("acc-null"))
                .thenReturn(Mono.just(ResponseEntity.ok(null)));
        // Aseguramos que si se llega a llamar withdrawal, no reviente
        org.mockito.Mockito.when(mockClient.AccountWithdrawal(org.mockito.Mockito.anyString(), org.mockito.Mockito.any()))
                .thenReturn(Mono.just(ResponseEntity.ok(new AccountBalanceResponse())));

        DebitCardWithdrawalOrchestrator orchestratorMock = new DebitCardWithdrawalOrchestrator(mockClient);
        DebitCard card = createBaseCard("acc-null");

        StepVerifier.create(orchestratorMock.withdrawCascade(card, new BigDecimal("10")))
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void mapHandlesNullBodyFromResponseEntity() {
        ExternalAccountsClient nullBodyClient = new ExternalAccountsClient() {
            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountWithdrawal(String accountId, BigDecimal amount) {
                return Mono.just(ResponseEntity.ok(new AccountBalanceResponse()));
            }

            @Override
            public Mono<ResponseEntity<AccountBalanceResponse>> AccountBalance(String accountId) {
                return Mono.just(ResponseEntity.ok(null));
            }
        };

        DebitCardWithdrawalOrchestrator orchestrator = new DebitCardWithdrawalOrchestrator(nullBodyClient);
        DebitCard card = createBaseCard("acc-null-body");

        StepVerifier.create(orchestrator.withdrawCascade(card, new BigDecimal("5")))
                .expectError(NullPointerException.class)
                .verify();
    }

    // Helper para evitar repetición de código
    private DebitCard createBaseCard(String primaryAccount) {
        return new DebitCard("card-id", "4111", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-id", "****", BigDecimal.ZERO, LocalDateTime.now(),
                primaryAccount, new LinkedHashSet<>());
    }

    private AccountBalanceResponse balance(String id, String amount) {
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setId(id);
        response.setBalance(new BigDecimal(amount));
        return response;
    }

    private AccountBalanceResponse balanceWithNullBalance(String id) {
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setId(id);
        response.setBalance(null);
        return response;
    }

    private static class StubAccountsClient implements ExternalAccountsClient {
        Map<String, AccountBalanceResponse> balances = new LinkedHashMap<>();
        Map<String, BigDecimal> withdrawals = new LinkedHashMap<>();

        @Override
        public Mono<ResponseEntity<AccountBalanceResponse>> AccountWithdrawal(String accountId, BigDecimal amount) {
            withdrawals.put(accountId, amount);
            return Mono.just(ResponseEntity.ok(balanceAfterWithdrawal(accountId, amount)));
        }

        @Override
        public Mono<ResponseEntity<AccountBalanceResponse>> AccountBalance(String accountId) {
            return Mono.just(ResponseEntity.ok(balances.get(accountId)));
        }

        private AccountBalanceResponse balanceAfterWithdrawal(String accountId, BigDecimal amount) {
            AccountBalanceResponse current = balances.get(accountId);
            if (current == null) {
                return null;
            }
            AccountBalanceResponse updated = new AccountBalanceResponse();
            updated.setId(accountId);
            updated.setBalance(current.getBalance().subtract(amount));
            return updated;
        }
    }
}
