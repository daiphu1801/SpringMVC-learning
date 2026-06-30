package com.examp.springmvc.order.application.query;

import java.math.BigDecimal;

public class OrderItemDTO {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final String productSku;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final BigDecimal subtotal;

    public OrderItemDTO(
            Long id,
            Long productId,
            String productName,
            String productSku,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public static OrderItemDTO fromDomain(com.examp.springmvc.order.domain.model.OrderItem item) {
        if (item == null) {
            return null;
        }
        return new OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductSku(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }
}
