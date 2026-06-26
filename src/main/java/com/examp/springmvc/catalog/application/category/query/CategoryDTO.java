package com.examp.springmvc.catalog.application.category.query;

public final class CategoryDTO {
    private final Long id;
    private final String name;
    private final String code;
    private final String description;

    public CategoryDTO(Long id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
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
