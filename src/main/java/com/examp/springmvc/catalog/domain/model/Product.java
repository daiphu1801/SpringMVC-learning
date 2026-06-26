package com.examp.springmvc.catalog.domain.model;

import java.math.BigDecimal;

public final class Product {
    private final Long id;
    private final Long categoryId;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final ProductStatus status;
    private final String imageUrl;

    public Product(
            Long id,
            Long categoryId,
            String sku,
            String name,
            String description,
            BigDecimal price,
            ProductStatus status,
            String imageUrl) {
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
        this.id = id;
        this.categoryId = categoryId;
        this.sku = sku.trim().toUpperCase();
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;
        this.status = status != null ? status : ProductStatus.ACTIVE;
        this.imageUrl = imageUrl;
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
}
