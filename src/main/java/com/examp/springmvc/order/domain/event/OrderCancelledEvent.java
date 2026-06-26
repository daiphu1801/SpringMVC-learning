package com.examp.springmvc.order.domain.event;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.shared.domain.DomainEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class OrderCancelledEvent implements DomainEvent {

    private final Order order;
    private final java.time.LocalDateTime occurredOn;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderCancelledEvent(Order order) {
        this.order = order;
        this.occurredOn = java.time.LocalDateTime.now();
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Order getOrder() {
        return order;
    }

    @Override
    public java.time.LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
