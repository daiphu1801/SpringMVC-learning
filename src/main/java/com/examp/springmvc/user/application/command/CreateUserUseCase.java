package com.examp.springmvc.user.application.command;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userCreateUserUseCase")
public class CreateUserUseCase implements CreateUserInputPort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordHasher passwordHasher;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CreateUserUseCase(UserPersistencePort userPersistencePort, PasswordHasher passwordHasher) {
        this.userPersistencePort = userPersistencePort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void execute(CreateUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command không được null");
        }

        Email email = new Email(command.getEmail());
        Password password = Password.fromRaw(command.getPassword(), passwordHasher);

        User user = new User();
        user.setUsername(command.getUsername());
        user.setFullName(command.getFullName());
        user.setEmail(email);
        user.setPhone(command.getPhone());
        user.setPassword(password);
        user.setRole(command.getRole());
        user.activate();

        user.validate();

        userPersistencePort.findByUsername(user.getUsername()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username đã tồn tại");
        });

        user.registered();
        userPersistencePort.save(user);
    }
}
