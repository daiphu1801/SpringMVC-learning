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
        User user = new User(
                "john",
                "John",
                new com.examp.springmvc.user.domain.model.Email("john@example.com"),
                "0987654321",
                null,
                com.examp.springmvc.user.domain.model.UserRole.USER);
        UserRegisteredEvent event = new UserRegisteredEvent(user);

        assertEquals("john", event.getUsername());
        assertEquals("john@example.com", event.getEmail());
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
