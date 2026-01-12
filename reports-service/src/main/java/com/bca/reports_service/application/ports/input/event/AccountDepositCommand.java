package com.bca.reports_service.application.ports.input.event;

import java.math.BigDecimal;

public record AccountDepositCommand(
        String accountId,
        BigDecimal amount,
        BigDecimal balance
) {}