package com.examp.springmvc.user.infrastructure.eventlistener;

import com.examp.springmvc.user.domain.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventListener.class);

    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        LOG.info("=== DOMAIN EVENT RECEIVED ===");
        LOG.info("User registered successfully: {}", event.getUser().getUsername());
        LOG.info("Email registered: {}", event.getUser().getEmail().getValue());
        LOG.info("Sending welcome email... (Simulated)");
        LOG.info("=============================");
    }
}
