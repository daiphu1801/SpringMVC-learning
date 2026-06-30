package com.examp.springmvc.auth.application.dto;

import com.examp.springmvc.user.domain.model.UserRole;
import java.io.Serializable;

public class AuthenticatedUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String fullName;
    private final String phone;
    private final String email;
    private final UserRole role;

    public AuthenticatedUserDTO(Long id, String username, String fullName, String phone, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
