package com.examp.springmvc.order.domain.event;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.shared.domain.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class OrderPaidEvent implements DomainEvent {

    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalAmount;
    private final LocalDateTime occurredOn;

    public OrderPaidEvent(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order không được null");
        }
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalAmount = order.getTotalAmount();
        this.occurredOn = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
