package com.bca.wallets_service.infrastructure.output.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "yanki_wallets")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class YankiWalletDocument {
    @Id
    private String id;
    private String walletId;
    private String customerId;
    private String phone;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
