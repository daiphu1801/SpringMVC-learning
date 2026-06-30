package com.examp.springmvc.user.application.usermanagement.command;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.dao.DataIntegrityViolationException;
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

        // 1. Kiểm tra sớm username trùng trước khi băm mật khẩu bằng BCrypt (tiết kiệm CPU)
        if (userPersistencePort.findByUsername(command.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        Email email = new Email(command.getEmail());
        Password password = Password.fromRaw(command.getPassword(), passwordHasher);

        User user = new User(
                command.getUsername(),
                command.getFullName(),
                email,
                command.getPhone(),
                password,
                com.examp.springmvc.user.domain.model.UserRole.valueOf(command.getRole()));

        user.validate();
        user.registered();

        // 2. Lưu và xử lý ngoại lệ vi phạm ràng buộc unique để tránh lỗi race condition
        try {
            userPersistencePort.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username đã tồn tại", e);
        }
    }
}
