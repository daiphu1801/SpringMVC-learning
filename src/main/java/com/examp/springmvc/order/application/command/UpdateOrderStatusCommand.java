package com.examp.springmvc.order.application.command;

public class UpdateOrderStatusCommand {

    private final Long orderId;
    private final String action;

    public UpdateOrderStatusCommand(Long orderId, String action) {
        this.orderId = orderId;
        this.action = action;
    }

    public Long getOrderId() {
        return orderId;
    }

    /**
     * Hành động Admin muốn thực hiện: "confirm", "ship", "deliver".
     */
    public String getAction() {
        return action;
    }
}
