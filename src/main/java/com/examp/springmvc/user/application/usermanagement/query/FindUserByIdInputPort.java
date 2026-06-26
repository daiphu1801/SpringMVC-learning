package com.examp.springmvc.user.application.usermanagement.query;

public interface FindUserByIdInputPort {

    UserDTO execute(Long id);
}
