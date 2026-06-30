package com.examp.springmvc.order.domain.event;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.shared.domain.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OrderPlacedEvent implements DomainEvent {

    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalAmount;
    private final ShippingAddress shippingAddress;
    private final PaymentMethod paymentMethod;
    private final List<OrderItem> items;
    private final LocalDateTime occurredOn;

    public OrderPlacedEvent(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order không được null");
        }
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalAmount = order.getTotalAmount();
        this.shippingAddress = order.getShippingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.items = Collections.unmodifiableList(new ArrayList<>(order.getItems()));
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

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
