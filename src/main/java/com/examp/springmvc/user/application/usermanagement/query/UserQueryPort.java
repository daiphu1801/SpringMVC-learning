package com.examp.springmvc.user.application.usermanagement.query;

import java.util.List;
import java.util.Optional;

public interface UserQueryPort {
    List<UserDTO> findAll();

    Optional<UserDTO> findById(Long id);
}
