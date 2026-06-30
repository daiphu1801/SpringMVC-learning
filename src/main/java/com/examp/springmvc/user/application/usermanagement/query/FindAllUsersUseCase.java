package com.examp.springmvc.user.application.usermanagement.query;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userFindAllUsersUseCase")
public class FindAllUsersUseCase implements FindAllUsersInputPort {

    private final UserQueryPort userQueryPort;

    public FindAllUsersUseCase(UserQueryPort userQueryPort) {
        this.userQueryPort = userQueryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> execute() {
        return userQueryPort.findAll();
    }
}
