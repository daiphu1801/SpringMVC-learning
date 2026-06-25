package com.examp.springmvc.user.application.usecase;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateUserUseCase {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CreateUserUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional
    public void execute(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }

        user.validate();

        userPersistencePort.findByUsername(user.getUsername()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username đã tồn tại");
        });

        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.activate();
        }

        userPersistencePort.save(user);
    }
}
