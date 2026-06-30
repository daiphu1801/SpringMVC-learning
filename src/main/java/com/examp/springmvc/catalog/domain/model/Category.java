package com.examp.springmvc.catalog.domain.model;

public final class Category {
    private final Long id;
    private String name;
    private String code;
    private String description;

    public Category(Long id, String name, String code, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        this.id = id;
        this.name = name.trim();
        this.code = code.trim().toLowerCase();
        this.description = description != null ? description.trim() : null;
    }

    public void updateDetails(String name, String code, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        this.name = name.trim();
        this.code = code.trim().toLowerCase();
        this.description = description != null ? description.trim() : null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
