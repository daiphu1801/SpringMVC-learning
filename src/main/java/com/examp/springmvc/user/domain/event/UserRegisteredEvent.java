package com.examp.springmvc.user.domain.event;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDateTime;

public final class UserRegisteredEvent implements DomainEvent {

    private final User user;
    private final LocalDateTime occurredOn;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UserRegisteredEvent(User user) {
        this.user = user;
        this.occurredOn = LocalDateTime.now();
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public User getUser() {
        return user;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
