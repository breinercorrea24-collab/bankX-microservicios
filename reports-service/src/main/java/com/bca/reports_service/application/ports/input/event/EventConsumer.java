package com.bca.reports_service.application.ports.input.event;

public interface EventConsumer {
    void publish(Object event);
}
