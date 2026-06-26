package com.examp.springmvc.catalog.application.category.command;

public final class CreateCategoryCommand {
    private final String name;
    private final String code;
    private final String description;

    public CreateCategoryCommand(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
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
