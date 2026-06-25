package com.examp.springmvc.user.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.examp.springmvc.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRegisteredEventTest {

    @Test
    @DisplayName("Should create event with user and occurredOn")
    void shouldCreateEvent() {
        User user = new User();
        UserRegisteredEvent event = new UserRegisteredEvent(user);

        assertEquals(user, event.getUser());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Should register event inside User aggregate root when registered is called")
    void shouldRegisterEventInUser() {
        User user = new User();
        assertTrue(user.getDomainEvents().isEmpty());

        user.registered();
        assertEquals(1, user.getDomainEvents().size());
        assertTrue(user.getDomainEvents().get(0) instanceof UserRegisteredEvent);

        user.clearDomainEvents();
        assertTrue(user.getDomainEvents().isEmpty());
    }
}
