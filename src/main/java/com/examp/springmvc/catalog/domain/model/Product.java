package com.examp.springmvc.catalog.domain.model;

import java.math.BigDecimal;

public final class Product {
    private final Long id;
    private Long categoryId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductStatus status;
    private String imageUrl;
    private Integer stock;

    public Product(
            Long id,
            Long categoryId,
            String sku,
            String name,
            String description,
            BigDecimal price,
            ProductStatus status,
            String imageUrl,
            Integer stock) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SKU không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không được âm");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không hợp lệ");
        }
        this.id = id;
        this.categoryId = categoryId;
        this.sku = sku.trim().toUpperCase();
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;
        this.status = status != null ? status : ProductStatus.ACTIVE;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    public void updateDetails(
            Long categoryId,
            String sku,
            String name,
            String description,
            BigDecimal price,
            ProductStatus status,
            String imageUrl,
            Integer stock) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SKU không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không được âm");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không hợp lệ");
        }
        this.categoryId = categoryId;
        this.sku = sku.trim().toUpperCase();
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;
        this.status = status != null ? status : ProductStatus.ACTIVE;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng trừ kho không được âm");
        }
        if (quantity > this.stock) {
            throw new IllegalArgumentException("Không đủ hàng tồn kho cho sản phẩm: " + this.name);
        }
        this.stock -= quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getStock() {
        return stock;
    }
}
