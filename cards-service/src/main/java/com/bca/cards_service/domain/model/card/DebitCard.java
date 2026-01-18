package com.bca.cards_service.domain.model.card;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.bca.cards_service.domain.enums.card.CardStatus;
import com.bca.cards_service.domain.enums.card.CardType;
import com.bca.cards_service.domain.exceptions.BusinessException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DebitCard extends Card {

    private String primaryAccountId;
    private Set<String> linkedAccountIds;



    public DebitCard(
            String id, String cardNumber, CardStatus status, CardType type, String customerId, 
            String maskedNumber, BigDecimal availableLimit, LocalDateTime createdAt,

            String primaryAccountId,
            Set<String> linkedAccountIds) {

        super(id, cardNumber, status, type, customerId, maskedNumber, availableLimit, createdAt);

        if (primaryAccountId == null) {
            throw new BusinessException("Primary account is required");
        }

        this.primaryAccountId = primaryAccountId;
        this.linkedAccountIds = linkedAccountIds != null
                ? linkedAccountIds
                : Set.of();
    }

    public List<String> getWithdrawalOrder() {
        List<String> order = new ArrayList<>();
        order.add(primaryAccountId);
        order.addAll(linkedAccountIds);
        return Collections.unmodifiableList(order);
    }

    public List<String> getDebitOrder() {
        List<String> order = new ArrayList<>();
        order.add(primaryAccountId);
        order.addAll(linkedAccountIds);
        return order;
    }

    public String getPrimaryAccountId() {
        return primaryAccountId;
    }

    public Set<String> getLinkedAccountIds() {
        return linkedAccountIds;
    }

    public boolean isAccountLinked(String accountId) {
        return primaryAccountId.equals(accountId)
                || linkedAccountIds.contains(accountId);
    }

    public boolean canUseAccount(String accountId) {
        return primaryAccountId.equals(accountId)
                || linkedAccountIds.contains(accountId);
    }


}
