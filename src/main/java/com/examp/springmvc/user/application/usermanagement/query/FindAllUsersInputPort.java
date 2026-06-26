package com.examp.springmvc.user.application.usermanagement.query;

import java.util.List;

public interface FindAllUsersInputPort {

    List<UserDTO> execute();
}
