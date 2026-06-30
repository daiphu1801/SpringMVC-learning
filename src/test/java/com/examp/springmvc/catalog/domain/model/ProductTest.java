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
                "http://example.com/image.png",
                100);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getCategoryId()).isEqualTo(2L);
        assertThat(product.getSku()).isEqualTo("IPHONE15");
        assertThat(product.getName()).isEqualTo("iPhone 15");
        assertThat(product.getDescription()).isEqualTo("Mô tả");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("20000000"));
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.getImageUrl()).isEqualTo("http://example.com/image.png");
        assertThat(product.getStock()).isEqualTo(100);
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
                        null,
                        100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã danh mục không được để trống");
    }

    @Test
    void shouldThrowExceptionWhenSkuIsEmpty() {
        assertThatThrownBy(() -> new Product(
                        1L, 2L, " ", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã SKU không được để trống");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> new Product(
                        1L,
                        2L,
                        "IPHONE15",
                        "iPhone 15",
                        "Mô tả",
                        new BigDecimal("-1"),
                        ProductStatus.ACTIVE,
                        null,
                        100))
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
                "http://example.com/image.png",
                100);

        product.updateDetails(
                3L,
                "IPHONE16",
                "iPhone 16",
                "Mô tả mới",
                new BigDecimal("25000000"),
                ProductStatus.INACTIVE,
                "http://example.com/image2.png",
                150);

        assertThat(product.getCategoryId()).isEqualTo(3L);
        assertThat(product.getSku()).isEqualTo("IPHONE16");
        assertThat(product.getName()).isEqualTo("iPhone 16");
        assertThat(product.getDescription()).isEqualTo("Mô tả mới");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("25000000"));
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.getImageUrl()).isEqualTo("http://example.com/image2.png");
        assertThat(product.getStock()).isEqualTo(150);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidDetails() {
        Product product = new Product(
                1L, 2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null, 100);

        assertThatThrownBy(() -> product.updateDetails(
                        null,
                        "IPHONE15",
                        "iPhone 15",
                        "Mô tả",
                        new BigDecimal("20000000"),
                        ProductStatus.ACTIVE,
                        null,
                        100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã danh mục không được để trống");

        assertThatThrownBy(() -> product.updateDetails(
                        2L, "IPHONE15", "", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tên sản phẩm không được để trống");

        assertThatThrownBy(() -> product.updateDetails(
                        2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("-100"), ProductStatus.ACTIVE, null, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Giá sản phẩm không được âm");

        assertThatThrownBy(() -> product.updateDetails(
                        2L,
                        "IPHONE15",
                        "iPhone 15",
                        "Mô tả",
                        new BigDecimal("20000000"),
                        ProductStatus.ACTIVE,
                        null,
                        -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Số lượng tồn kho không hợp lệ");
    }

    @Test
    void shouldDecreaseStockSuccessfully() {
        Product product = new Product(
                1L, 2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null, 100);
        product.decreaseStock(30);
        assertThat(product.getStock()).isEqualTo(70);
    }

    @Test
    void shouldThrowExceptionWhenDecreasingMoreThanStock() {
        Product product = new Product(
                1L, 2L, "IPHONE15", "iPhone 15", "Mô tả", new BigDecimal("20000000"), ProductStatus.ACTIVE, null, 50);
        assertThatThrownBy(() -> product.decreaseStock(60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Không đủ hàng tồn kho");
    }
}
