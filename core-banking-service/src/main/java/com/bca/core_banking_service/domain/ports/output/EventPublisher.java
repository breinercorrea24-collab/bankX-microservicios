package com.bca.core_banking_service.domain.ports.output;

public interface EventPublisher {

    void publish(Object event);
}
