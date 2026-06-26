package com.examp.springmvc.order.domain.event;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.shared.domain.DomainEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class OrderStatusChangedEvent implements DomainEvent {

    private final Order order;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final java.time.LocalDateTime occurredOn;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderStatusChangedEvent(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.occurredOn = java.time.LocalDateTime.now();
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Order getOrder() {
        return order;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public java.time.LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
