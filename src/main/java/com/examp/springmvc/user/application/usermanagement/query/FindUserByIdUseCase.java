package com.examp.springmvc.user.application.usermanagement.query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userFindUserByIdUseCase")
public class FindUserByIdUseCase implements FindUserByIdInputPort {

    private final UserQueryPort userQueryPort;

    public FindUserByIdUseCase(UserQueryPort userQueryPort) {
        this.userQueryPort = userQueryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }
        return userQueryPort
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + id));
    }
}
