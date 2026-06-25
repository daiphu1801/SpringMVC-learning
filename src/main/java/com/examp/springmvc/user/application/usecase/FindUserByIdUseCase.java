package com.examp.springmvc.user.application.usecase;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FindUserByIdUseCase {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindUserByIdUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional(readOnly = true)
    public User execute(Long id) {
        return userPersistencePort
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + id));
    }
}
