package com.examp.springmvc.order.application.query;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private final Long id;
    private final Long userId;
    private final String status;
    private final BigDecimal totalAmount;
    private final String receiverName;
    private final String receiverPhone;
    private final String shippingAddress;
    private final String note;
    private final String paymentMethod;
    private final String paymentStatus;
    private final List<OrderItemDTO> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderDTO(
            Long id,
            Long userId,
            String status,
            BigDecimal totalAmount,
            String receiverName,
            String receiverPhone,
            String shippingAddress,
            String note,
            String paymentMethod,
            String paymentStatus,
            List<OrderItemDTO> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.shippingAddress = shippingAddress;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public List<OrderItemDTO> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static OrderDTO fromDomain(com.examp.springmvc.order.domain.model.Order order) {
        if (order == null) {
            return null;
        }
        List<OrderItemDTO> itemDtos =
                order.getItems().stream().map(OrderItemDTO::fromDomain).collect(java.util.stream.Collectors.toList());
        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getShippingAddress().getReceiverName(),
                order.getShippingAddress().getReceiverPhone(),
                order.getShippingAddress().getFullAddress(),
                order.getNote(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus().name(),
                itemDtos,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
