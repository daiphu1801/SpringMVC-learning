package com.examp.springmvc.order.domain.event;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.shared.domain.DomainEvent;
import java.time.LocalDateTime;

public final class OrderStatusChangedEvent implements DomainEvent {

    private final Long orderId;
    private final Long userId;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final ShippingAddress shippingAddress;
    private final LocalDateTime occurredOn;

    public OrderStatusChangedEvent(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        if (order == null) {
            throw new IllegalArgumentException("Order không được null");
        }
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.shippingAddress = order.getShippingAddress();
        this.occurredOn = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
