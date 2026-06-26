package com.examp.springmvc.order.domain.model;

import java.math.BigDecimal;

/**
 * Entity con nằm bên trong Aggregate Order.
 * Không thể tồn tại độc lập, chỉ được truy cập qua Order (Aggregate Root).
 * Chứa snapshot dữ liệu sản phẩm tại thời điểm đặt hàng để đảm bảo toàn vẹn lịch sử.
 */
public final class OrderItem {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final String productSku;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(
            Long id, Long productId, String productName, String productSku, BigDecimal unitPrice, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (productSku == null || productSku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU sản phẩm không được để trống");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá sản phẩm không được âm");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.id = id;
        this.productId = productId;
        this.productName = productName.trim();
        this.productSku = productSku.trim().toUpperCase();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Nghiệp vụ tính thành tiền của dòng đơn hàng.
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
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
}
