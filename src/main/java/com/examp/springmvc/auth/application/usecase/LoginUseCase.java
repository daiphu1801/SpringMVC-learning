package com.examp.springmvc.auth.application.usecase;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.auth.application.ports.input.LoginInputPort;
import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoginUseCase implements LoginInputPort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordHasher passwordHasher;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public LoginUseCase(UserPersistencePort userPersistencePort, PasswordHasher passwordHasher) {
        this.userPersistencePort = userPersistencePort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUserDTO execute(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        User user = userPersistencePort
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác"));

        if (user.getStatus() != com.examp.springmvc.user.domain.model.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản đang bị khóa");
        }

        if (!user.getPassword().match(rawPassword, passwordHasher)) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        return new AuthenticatedUserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhone(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getRole());
    }
}
