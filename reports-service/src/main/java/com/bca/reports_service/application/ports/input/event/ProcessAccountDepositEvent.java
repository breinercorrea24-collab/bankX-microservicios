package com.bca.reports_service.application.ports.input.event;

public interface ProcessAccountDepositEvent {
    void process(AccountDepositCommand command);
}
