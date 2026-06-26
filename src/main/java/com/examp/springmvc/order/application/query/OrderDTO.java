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

    public String getFormattedPaymentMethod() {
        if ("VIETQR".equals(paymentMethod)) {
            return "Chuyển khoản VietQR";
        } else if ("CASH".equals(paymentMethod)) {
            return "Tiền mặt khi nhận hàng";
        }
        return paymentMethod != null ? paymentMethod : "";
    }

    public String getFormattedPaymentStatus() {
        if ("PAID".equals(paymentStatus)) {
            return "Đã thanh toán";
        } else if ("PENDING".equals(paymentStatus)) {
            return "Chờ thanh toán";
        }
        return paymentStatus != null ? paymentStatus : "";
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

    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return "";
        }
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    public String getFormattedUpdatedAt() {
        if (updatedAt == null) {
            return "";
        }
        return updatedAt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }
}
