package com.examp.springmvc.user.application.query;

public interface FindUserByIdInputPort {

    UserDTO execute(Long id);
}
