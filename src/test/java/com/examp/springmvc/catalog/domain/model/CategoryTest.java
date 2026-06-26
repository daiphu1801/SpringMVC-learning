package com.examp.springmvc.catalog.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void shouldCreateCategorySuccessfully() {
        Category category = new Category(1L, "Điện thoại", "dien-thoai", "Mô tả điện thoại");

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Điện thoại");
        assertThat(category.getCode()).isEqualTo("dien-thoai");
        assertThat(category.getDescription()).isEqualTo("Mô tả điện thoại");
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThatThrownBy(() -> new Category(1L, "", "dien-thoai", "Mô tả"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tên danh mục không được để trống");
    }

    @Test
    void shouldThrowExceptionWhenCodeIsEmpty() {
        assertThatThrownBy(() -> new Category(1L, "Điện thoại", "  ", "Mô tả"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã danh mục không được để trống");
    }
}
