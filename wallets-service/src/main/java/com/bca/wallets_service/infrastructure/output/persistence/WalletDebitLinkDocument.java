package com.bca.wallets_service.infrastructure.output.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "wallet_debit_links")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDebitLinkDocument {
    @Id
    private String id;
    private String walletId;
    private String walletType;
    private String debitCardId;
    private String mainAccountId;
    private String status;
    private LocalDateTime linkedAt;
}