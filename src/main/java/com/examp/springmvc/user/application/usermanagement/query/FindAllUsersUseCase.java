package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.shared.domain.dto.PagedResult;
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
    public PagedResult<UserDTO> execute(int page, int size) {
        return userQueryPort.findPaged(page, size);
    }
}
