package com.examp.springmvc.user.application.usecase;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateUserUseCase {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UpdateUserUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional
    public void execute(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }

        user.validate();

        userPersistencePort
                .findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + user.getId()));

        userPersistencePort.save(user);
    }
}
