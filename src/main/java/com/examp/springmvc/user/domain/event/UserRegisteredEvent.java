package com.examp.springmvc.user.domain.event;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.model.User;
import java.time.LocalDateTime;

public final class UserRegisteredEvent implements DomainEvent {

    private final Long userId;
    private final String username;
    private final String email;
    private final LocalDateTime occurredOn;

    public UserRegisteredEvent(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail() != null ? user.getEmail().getValue() : null;
        this.occurredOn = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
