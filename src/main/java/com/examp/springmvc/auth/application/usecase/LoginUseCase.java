package com.examp.springmvc.auth.application.usecase;

import com.examp.springmvc.auth.application.ports.input.LoginInputPort;
import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDataAccessMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoginUseCase implements LoginInputPort {

    private final UserQueryMapper userQueryMapper;
    private final PasswordHasher passwordHasher;
    private final UserDataAccessMapper userDataAccessMapper;

    public LoginUseCase(
            UserQueryMapper userQueryMapper, PasswordHasher passwordHasher, UserDataAccessMapper userDataAccessMapper) {
        this.userQueryMapper = userQueryMapper;
        this.passwordHasher = passwordHasher;
        this.userDataAccessMapper = userDataAccessMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public User execute(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        UserDbEntity entity = userQueryMapper.findByUsername(username);
        if (entity == null) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        User user = userDataAccessMapper.toDomain(entity);

        if (user.getStatus() != com.examp.springmvc.user.domain.model.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản đang bị khóa");
        }

        if (!user.getPassword().match(rawPassword, passwordHasher)) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        return user;
    }
}
