package com.examp.springmvc.user.application.usermanagement.command;

import java.util.ArrayList;
import java.util.List;

public final class UserImportRow {
    private final int rowNum;
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String password;
    private String role;
    private String status;
    private final List<String> rowErrors = new ArrayList<>();

    public UserImportRow(
            int rowNum,
            String username,
            String fullName,
            String email,
            String phone,
            String password,
            String role,
            String status) {
        this.rowNum = rowNum;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public int getRowNum() {
        return rowNum;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addError(String err) {
        rowErrors.add(err);
    }

    public boolean hasErrors() {
        return !rowErrors.isEmpty();
    }

    public String getErrorMessage() {
        return String.join(", ", rowErrors);
    }
}
