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
public final class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String fullName;
    private Email email;
    private String phone;
    private UserStatus status = UserStatus.ACTIVE;
    private Password password;
    private UserRole role = UserRole.USER;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();
    private final List<Address> addresses = new ArrayList<>();

    @Deprecated
    public User() {}

    public User(String username, String fullName, Email email, String phone, Password password, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role != null ? role : UserRole.USER;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public User(
            Long id,
            String username,
            String fullName,
            Email email,
            String phone,
            UserStatus status,
            Password password,
            UserRole role,
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
        validate();
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
        if (role == null) {
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
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID không được null");
        }
        if (this.id != null) {
            throw new IllegalStateException("ID đã được gán và không thể thay đổi");
        }
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void updateProfile(String fullName, String phone, Email email) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void changeStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public Password getPassword() {
        return password;
    }

    public void changePassword(Password password) {
        if (password == null) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public UserRole getRole() {
        return role;
    }

    public void changeRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
