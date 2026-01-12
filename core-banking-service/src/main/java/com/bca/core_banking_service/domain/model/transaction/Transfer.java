package com.bca.core_banking_service.domain.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.transaction.TransferType;

public class Transfer {

    private String id;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private TransferType type;
    private LocalDateTime executedAt;
}
