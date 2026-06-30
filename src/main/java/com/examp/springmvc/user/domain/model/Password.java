package com.examp.springmvc.user.domain.model;

import com.examp.springmvc.auth.domain.PasswordHasher;
import java.io.Serializable;
import java.util.Objects;

public final class Password implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String hashedPassword;

    private Password(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        this.hashedPassword = hashedPassword;
    }

    public static Password fromHashed(String hashedPassword) {
        return new Password(hashedPassword);
    }

    public static Password fromRaw(String rawPassword, PasswordHasher hasher) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (rawPassword.length() < 6 || rawPassword.length() > 50) {
            throw new IllegalArgumentException("Mật khẩu phải từ 6 đến 50 ký tự");
        }
        if (!Character.isUpperCase(rawPassword.charAt(0))) {
            throw new IllegalArgumentException("Mật khẩu phải bắt đầu bằng chữ in hoa");
        }
        if (!rawPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ số");
        }
        if (!rawPassword.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một ký tự đặc biệt");
        }
        return new Password(hasher.hash(rawPassword));
    }

    public String getHashedValue() {
        return hashedPassword;
    }

    public boolean match(String rawPassword, PasswordHasher hasher) {
        if (rawPassword == null) {
            return false;
        }
        return hasher.check(rawPassword, this.hashedPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Password password = (Password) o;
        return Objects.equals(hashedPassword, password.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedPassword);
    }
}
