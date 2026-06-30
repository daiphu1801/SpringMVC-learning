package com.examp.springmvc.order.application.command;

public class UpdateOrderStatusCommand {

    private final Long orderId;
    private final OrderStatusAction action;

    public UpdateOrderStatusCommand(Long orderId, OrderStatusAction action) {
        this.orderId = orderId;
        this.action = action;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatusAction getAction() {
        return action;
    }
}
