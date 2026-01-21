package com.bca.cards_service.application.usecases;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;
import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.card.CreditCard;
import com.bca.cards_service.domain.model.card.DebitCard;
import com.bca.cards_service.domain.model.ports.CardRepository;
import com.bca.cards_service.domain.model.ports.rest.ExternalAccountsClient;
import com.bca.cards_service.infrastructure.output.rest.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentCardsDebitUseCaseTest {

    private RecordingCardRepository cardRepository;
    private StubAccountsClient accountsClient;
    private PaymentCardsDebitUseCase useCase;

    @BeforeEach
    void setUp() {
        cardRepository = new RecordingCardRepository();
        accountsClient = new StubAccountsClient();
        useCase = new PaymentCardsDebitUseCase(cardRepository, accountsClient);
    }

    @Test
    void payWithdrawsWhenCardIsActiveDebit() {
        DebitCard card = new DebitCard("card-1", "4111", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-1", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(Set.of("acc-2")));
        cardRepository.findByIdResult = Mono.just(card);
        accountsClient.balances.put("acc-1", balance("acc-1", "20"));
        accountsClient.balances.put("acc-2", balance("acc-2", "20"));

        StepVerifier.create(useCase.pay("card-1", new BigDecimal("30")))
                .verifyComplete();

        assertEquals(new BigDecimal("20"), accountsClient.withdrawals.get("acc-1"));
        assertEquals(new BigDecimal("10"), accountsClient.withdrawals.get("acc-2"));
    }

    @Test
    void withdrawFailsWhenCardBlocked() {
        DebitCard card = new DebitCard("card-2", "4222", CardStatus.BLOCKED, CardType.DEBIT,
                "cust-2", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(Set.of("acc-2")));
        cardRepository.findByIdResult = Mono.just(card);

        StepVerifier.create(useCase.withdraw("card-2", new BigDecimal("5")))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void withdrawFailsWhenCardIsNotDebit() {
        Card creditCard = new CreditCard("card-3", "4333", CardStatus.ACTIVE, CardType.CREDIT,
                "cust-3", "****", new BigDecimal("50.00"), LocalDateTime.now(), new BigDecimal("50.00"));
        cardRepository.findByIdResult = Mono.just(creditCard);

        StepVerifier.create(useCase.withdraw("card-3", new BigDecimal("5")))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void withdrawProcessesWhenCardIsActiveDebit() {
        DebitCard card = new DebitCard("card-4", "4444", CardStatus.ACTIVE, CardType.DEBIT,
                "cust-4", "****", BigDecimal.ZERO, LocalDateTime.now(),
                "acc-1", new LinkedHashSet<>(Set.of()));
        cardRepository.findByIdResult = Mono.just(card);
        accountsClient.balances.put("acc-1", balance("acc-1", "15"));

        StepVerifier.create(useCase.withdraw("card-4", new BigDecimal("10")))
                .verifyComplete();

        assertEquals(new BigDecimal("10"), accountsClient.withdrawals.get("acc-1"));
    }

    private AccountBalanceResponse balance(String id, String amount) {
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setId(id);
        response.setBalance(new BigDecimal(amount));
        return response;
    }

    private static class RecordingCardRepository implements CardRepository {
        Mono<Card> saveResult = Mono.empty();
        Mono<Card> findByIdResult = Mono.empty();
        Mono<Boolean> findByCustomerResult = Mono.just(false);

        @Override
        public Mono<Card> save(Card card) {
            return saveResult;
        }

        @Override
        public Mono<Card> findById(String cardId) {
            return findByIdResult;
        }

        @Override
        public Mono<Boolean> findByCustomerId(String customerId) {
            return findByCustomerResult;
        }
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
