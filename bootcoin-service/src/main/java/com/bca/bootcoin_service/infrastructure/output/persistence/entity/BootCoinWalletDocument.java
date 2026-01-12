package com.bca.bootcoin_service.infrastructure.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "bootcoin_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootCoinWalletDocument {

    @Id
    private String walletId;
    private String customerId;
    private String document;
    private BigDecimal balanceBTC;
    private String status;
    private LocalDateTime createdAt;

}
