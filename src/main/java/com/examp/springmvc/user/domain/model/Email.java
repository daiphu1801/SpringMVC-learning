package com.examp.springmvc.user.domain.model;

import java.io.Serializable;
import java.util.Objects;

public final class Email implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        String trimmed = value.trim();
        if (!trimmed.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Định dạng email không hợp lệ");
        }
        this.value = trimmed;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
