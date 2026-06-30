package com.examp.springmvc.user.application.usermanagement.query;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO() {}

    public UserDTO(
            Long id,
            String username,
            String fullName,
            String email,
            String phone,
            String status,
            String role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
