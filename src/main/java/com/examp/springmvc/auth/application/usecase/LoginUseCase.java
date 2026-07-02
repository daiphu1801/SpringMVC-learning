package com.examp.springmvc.auth.application.usecase;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.auth.application.ports.input.LoginInputPort;
import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoginUseCase implements LoginInputPort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordHasher passwordHasher;

    private final ConcurrentHashMap<String, LockoutAttempt> lockoutMap = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15L * 60 * 1000; // 15 minutes
    private static final String DUMMY_HASH = "$2a$10$NX37r/W87.uA7Z.tVfRFO.8D6o4aYV7Ew5D09tJ7i64lXv8q6f7gW";

    private static class LockoutAttempt {
        int attempts;
        long lockoutEndTime;

        LockoutAttempt(int attempts, long lockoutEndTime) {
            this.attempts = attempts;
            this.lockoutEndTime = lockoutEndTime;
        }
    }

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

        // Lockout Check
        LockoutAttempt lockout = lockoutMap.get(username);
        if (lockout != null && lockout.lockoutEndTime > System.currentTimeMillis()) {
            long remainingMinutes = (lockout.lockoutEndTime - System.currentTimeMillis()) / 60000 + 1;
            throw new IllegalArgumentException(
                    "Tài khoản đang bị khóa tạm thời. Vui lòng quay lại sau " + remainingMinutes + " phút.");
        }

        Optional<User> userOpt = userPersistencePort.findByUsername(username);

        boolean dummyCheck = false;
        String hashedPasswordToMatch;
        User user = null;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            hashedPasswordToMatch = user.getPassword().getHashedValue();
        } else {
            hashedPasswordToMatch = DUMMY_HASH;
            dummyCheck = true;
        }

        // Verify password
        boolean passwordMatches = passwordHasher.check(rawPassword, hashedPasswordToMatch);

        if (dummyCheck || !passwordMatches) {
            recordFailedAttempt(username);
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() != com.examp.springmvc.user.domain.model.UserStatus.ACTIVE) {
            recordFailedAttempt(username);
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        // Clear lockout record on success
        lockoutMap.remove(username);

        return new AuthenticatedUserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhone(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getRole());
    }

    private void recordFailedAttempt(String username) {
        lockoutMap.compute(username, (k, v) -> {
            if (v == null) {
                return new LockoutAttempt(1, 0);
            }
            if (v.lockoutEndTime > 0 && v.lockoutEndTime <= System.currentTimeMillis()) {
                return new LockoutAttempt(1, 0);
            }
            v.attempts++;
            if (v.attempts >= MAX_ATTEMPTS) {
                v.lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION_MS;
            }
            return v;
        });
    }
}
