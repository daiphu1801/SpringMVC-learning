package com.examp.springmvc.user.application.usermanagement.command;

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

        existing.updateProfile(command.getFullName(), command.getPhone(), email);
        existing.changeRole(com.examp.springmvc.user.domain.model.UserRole.valueOf(command.getRole()));
        existing.changeStatus(com.examp.springmvc.user.domain.model.UserStatus.valueOf(command.getStatus()));

        if (command.getPassword() != null && !command.getPassword().trim().isEmpty()) {
            existing.changePassword(Password.fromRaw(command.getPassword(), passwordHasher));
        }

        existing.validate();

        userPersistencePort.save(existing);
    }
}
