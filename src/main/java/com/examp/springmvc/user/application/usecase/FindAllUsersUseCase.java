package com.examp.springmvc.user.application.usecase;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FindAllUsersUseCase {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindAllUsersUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional(readOnly = true)
    public List<User> execute() {
        return userPersistencePort.findAll();
    }
}
