package com.examp.springmvc.catalog.application.product.command;

import com.examp.springmvc.catalog.domain.model.ProductStatus;
import java.io.InputStream;
import java.math.BigDecimal;

public final class UpdateProductCommand {
    private final Long id;
    private final Long categoryId;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final ProductStatus status;
    private final InputStream imageStream;
    private final String imageName;

    public UpdateProductCommand(
            Long id,
            Long categoryId,
            String sku,
            String name,
            String description,
            BigDecimal price,
            ProductStatus status,
            InputStream imageStream,
            String imageName) {
        this.id = id;
        this.categoryId = categoryId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.imageStream = imageStream;
        this.imageName = imageName;
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

    public InputStream getImageStream() {
        return imageStream;
    }

    public String getImageName() {
        return imageName;
    }
}
