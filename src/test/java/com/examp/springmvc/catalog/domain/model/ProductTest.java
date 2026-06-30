package com.examp.springmvc.catalog.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {
        Product product = new Product(
                1L,
                2L,
                "IPHONE15",
                "iPhone 15",
                "Mô tả",
                new BigDecimal("20000000"),
                ProductStatus.ACTIVE,
                "http://example.com/image.png");

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getCategoryId()).isEqualTo(2L);
        assertThat(product.getSku()).isEqualTo("IPHONE15");
        assertThat(product.getName()).isEqualTo("iPhone 15");
        assertThat(product.getDescription()).isEqualTo("Mô tả");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("20000000"));
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.getImageUrl()).isEqualTo("http://example.com/image.png");
    }

    @Test
    void shouldThrowExceptionWhenCategoryIdIsNull() {
        assertThatThrownBy(() -> new Product(
                        1L,
                        null,
                        "IPHONE15",
                        "iPhone 15",
                        "Mô tả",
                        new BigDecimal("20000000"),
                        ProductStatus.ACTIVE,
                        null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã danh mục không được để trống");
    }

    @Test
    void shouldThrowExceptionWhenSkuIsEmpty() {
        assertThatThrownBy(() -> new Product(
                        1L, 2L, " ", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã SKU không được để trống");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> new Product(
                        1L, 2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("-1"), ProductStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Giá sản phẩm không được âm");
    }

    @Test
    void shouldUpdateDetailsSuccessfully() {
        Product product = new Product(
                1L,
                2L,
                "IPHONE15",
                "iPhone 15",
                "Mô tả",
                new BigDecimal("20000000"),
                ProductStatus.ACTIVE,
                "http://example.com/image.png");

        product.updateDetails(
                3L,
                "IPHONE16",
                "iPhone 16",
                "Mô tả mới",
                new BigDecimal("25000000"),
                ProductStatus.INACTIVE,
                "http://example.com/image2.png");

        assertThat(product.getCategoryId()).isEqualTo(3L);
        assertThat(product.getSku()).isEqualTo("IPHONE16");
        assertThat(product.getName()).isEqualTo("iPhone 16");
        assertThat(product.getDescription()).isEqualTo("Mô tả mới");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("25000000"));
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.getImageUrl()).isEqualTo("http://example.com/image2.png");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidDetails() {
        Product product = new Product(
                1L, 2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null);

        assertThatThrownBy(() -> product.updateDetails(
                        null, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã danh mục không được để trống");

        assertThatThrownBy(() -> product.updateDetails(
                        2L, "IPHONE15", "", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tên sản phẩm không được để trống");

        assertThatThrownBy(() -> product.updateDetails(
                        2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("-100"), ProductStatus.ACTIVE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Giá sản phẩm không được âm");
    }
}
