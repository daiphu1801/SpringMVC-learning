package com.examp.springmvc.user.domain.model;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.event.UserAddressAddedEvent;
import com.examp.springmvc.user.domain.event.UserAddressRemovedEvent;
import com.examp.springmvc.user.domain.event.UserRegisteredEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String fullName;
    private Email email;
    private String phone;
    private String status = "ACTIVE";
    private Password password;
    private String role = "USER";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();
    private final List<Address> addresses = new ArrayList<>();

    public User() {}

    public User(
            Long id,
            String username,
            String fullName,
            Email email,
            String phone,
            String status,
            Password password,
            String role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (role == null || (!role.equals("USER") && !role.equals("ADMIN"))) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
    }

    public void registered() {
        registerEvent(new UserRegisteredEvent(this));
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void deactivate() {
        this.status = "INACTIVE";
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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
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

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
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

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void setAddresses(List<Address> newAddresses) {
        this.addresses.clear();
        if (newAddresses != null) {
            this.addresses.addAll(newAddresses);
        }
    }

    public void addAddress(Address address) {
        if (address == null) {
            return;
        }
        if (addresses.size() >= 5) {
            throw new IllegalArgumentException("Không thể thêm quá 5 địa chỉ");
        }

        boolean makeDefault = address.isDefault() || addresses.isEmpty();

        if (makeDefault) {
            for (int i = 0; i < addresses.size(); i++) {
                addresses.set(i, addresses.get(i).withDefault(false));
            }
        }

        Address addressToAdd = address.withDefault(makeDefault);
        addresses.add(addressToAdd);

        registerEvent(new UserAddressAddedEvent(this, addressToAdd));
    }

    public void removeAddress(Long addressId) {
        if (addressId == null) {
            return;
        }
        Address toRemove = null;
        for (Address addr : addresses) {
            if (addressId.equals(addr.getId())) {
                toRemove = addr;
                break;
            }
        }
        if (toRemove == null) {
            return;
        }

        addresses.remove(toRemove);

        if (toRemove.isDefault() && !addresses.isEmpty()) {
            addresses.set(0, addresses.get(0).withDefault(true));
        }

        registerEvent(new UserAddressRemovedEvent(this, toRemove));
    }
}
