package com.examp.springmvc.user.application.command;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userUpdateUserUseCase")
public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordHasher passwordHasher;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UpdateUserUseCase(UserPersistencePort userPersistencePort, PasswordHasher passwordHasher) {
        this.userPersistencePort = userPersistencePort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void execute(UpdateUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command không được null");
        }
        if (command.getId() == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }

        User existing = userPersistencePort
                .findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + command.getId()));

        Email email = new Email(command.getEmail());

        User user = new User();
        user.setId(command.getId());
        user.setUsername(command.getUsername());
        user.setFullName(command.getFullName());
        user.setEmail(email);
        user.setPhone(command.getPhone());
        user.setStatus(command.getStatus());
        user.setRole(command.getRole());

        if (command.getPassword() == null || command.getPassword().trim().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(Password.fromRaw(command.getPassword(), passwordHasher));
        }

        user.validate();

        userPersistencePort.save(user);
    }
}
