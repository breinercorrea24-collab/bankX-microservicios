package com.bca.wallets_service.domain.model;

import java.time.LocalDateTime;

public class WalletDebitLink {
    private String walletId;
    private WalletType walletType;
    private String debitCardId;
    private String mainAccountId;
    private LinkStatus status;
    private LocalDateTime linkedAt;

    public WalletDebitLink(String walletId, WalletType walletType, String debitCardId, String mainAccountId, LinkStatus status, LocalDateTime linkedAt) {
        this.walletId = walletId;
        this.walletType = walletType;
        this.debitCardId = debitCardId;
        this.mainAccountId = mainAccountId;
        this.status = status;
        this.linkedAt = linkedAt;
    }

    // Getters and setters
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public WalletType getWalletType() { return walletType; }
    public void setWalletType(WalletType walletType) { this.walletType = walletType; }

    public String getDebitCardId() { return debitCardId; }
    public void setDebitCardId(String debitCardId) { this.debitCardId = debitCardId; }

    public String getMainAccountId() { return mainAccountId; }
    public void setMainAccountId(String mainAccountId) { this.mainAccountId = mainAccountId; }

    public LinkStatus getStatus() { return status; }
    public void setStatus(LinkStatus status) { this.status = status; }

    public LocalDateTime getLinkedAt() { return linkedAt; }
    public void setLinkedAt(LocalDateTime linkedAt) { this.linkedAt = linkedAt; }

    public enum WalletType {
        YANKI, BOOTCOIN
    }

    public enum LinkStatus {
        LINKED, UNLINKED
    }
}