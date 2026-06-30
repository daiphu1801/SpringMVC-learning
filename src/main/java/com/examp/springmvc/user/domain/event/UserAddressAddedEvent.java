package com.examp.springmvc.user.domain.event;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.model.Address;
import com.examp.springmvc.user.domain.model.User;
import java.time.LocalDateTime;

public final class UserAddressAddedEvent implements DomainEvent {

    private final Long userId;
    private final Address address;
    private final LocalDateTime occurredOn;

    public UserAddressAddedEvent(User user, Address address) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        this.userId = user.getId();
        this.address = address;
        this.occurredOn = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
