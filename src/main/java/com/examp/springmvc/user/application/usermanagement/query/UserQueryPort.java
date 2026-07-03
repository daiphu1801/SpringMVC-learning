package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.shared.domain.dto.PagedResult;
import java.util.List;
import java.util.Optional;

public interface UserQueryPort {
    List<UserDTO> findAll();

    PagedResult<UserDTO> findPaged(int page, int size);

    Optional<UserDTO> findById(Long id);
}
