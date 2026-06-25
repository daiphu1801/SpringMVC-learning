package com.examp.springmvc.user.application.command;

import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDeleteUserUseCase")
public class DeleteUserUseCase implements DeleteUserInputPort {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DeleteUserUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional
    public void execute(DeleteUserCommand command) {
        if (command == null || command.getId() == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }

        userPersistencePort
                .findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + command.getId()));

        userPersistencePort.deleteById(command.getId());
    }
}
