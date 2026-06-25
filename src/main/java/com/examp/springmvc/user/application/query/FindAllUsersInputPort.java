package com.examp.springmvc.user.application.query;

import java.util.List;

public interface FindAllUsersInputPort {

    List<UserDTO> execute();
}
