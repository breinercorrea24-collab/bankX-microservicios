package com.bca.wallets_service.domain.model;

public class DebitCard {
    private String debitCardId;
    private String mainAccountId;
    private String customerId;

    public DebitCard(String debitCardId, String mainAccountId, String customerId) {
        this.debitCardId = debitCardId;
        this.mainAccountId = mainAccountId;
        this.customerId = customerId;
    }

    // Getters and setters
    public String getDebitCardId() { return debitCardId; }
    public void setDebitCardId(String debitCardId) { this.debitCardId = debitCardId; }

    public String getMainAccountId() { return mainAccountId; }
    public void setMainAccountId(String mainAccountId) { this.mainAccountId = mainAccountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
}