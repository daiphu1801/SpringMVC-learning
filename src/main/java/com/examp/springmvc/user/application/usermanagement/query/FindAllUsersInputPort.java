package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.shared.domain.dto.PagedResult;

public interface FindAllUsersInputPort {

    PagedResult<UserDTO> execute(int page, int size);
}
