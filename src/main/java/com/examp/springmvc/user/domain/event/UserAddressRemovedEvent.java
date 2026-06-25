package com.examp.springmvc.user.domain.event;

import com.examp.springmvc.shared.domain.DomainEvent;
import com.examp.springmvc.user.domain.model.Address;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDateTime;

public final class UserAddressRemovedEvent implements DomainEvent {

    private final User user;
    private final Address address;
    private final LocalDateTime occurredOn;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UserAddressRemovedEvent(User user, Address address) {
        this.user = user;
        this.address = address;
        this.occurredOn = LocalDateTime.now();
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public User getUser() {
        return user;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
