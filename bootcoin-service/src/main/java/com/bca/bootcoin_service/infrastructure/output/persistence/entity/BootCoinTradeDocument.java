package com.bca.bootcoin_service.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "bootcoin_trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootCoinTradeDocument {

    @Id
    private String tradeId;
    private String tradeType;
    private BigDecimal amountBTC;
    private BigDecimal pricePEN;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String buyerWalletId;
    private String sellerWalletId;

}
