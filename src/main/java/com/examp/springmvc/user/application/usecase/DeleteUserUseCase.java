package com.examp.springmvc.user.application.usecase;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUserUseCase {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DeleteUserUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional
    public void execute(Long id) {
        userPersistencePort
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + id));

        userPersistencePort.deleteById(id);
    }
}
