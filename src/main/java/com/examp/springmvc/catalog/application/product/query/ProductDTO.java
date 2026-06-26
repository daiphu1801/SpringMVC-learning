package com.examp.springmvc.catalog.application.product.query;

import com.examp.springmvc.catalog.domain.model.ProductStatus;
import java.math.BigDecimal;

public final class ProductDTO {
    private final Long id;
    private final Long categoryId;
    private final String categoryName;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final ProductStatus status;
    private final String imageUrl;

    public ProductDTO(
            Long id,
            Long categoryId,
            String categoryName,
            String sku,
            String name,
            String description,
            BigDecimal price,
            ProductStatus status,
            String imageUrl) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
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

    public String getStatusDesc() {
        return status == ProductStatus.ACTIVE ? "Hoạt động" : "Ngừng kinh doanh";
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
