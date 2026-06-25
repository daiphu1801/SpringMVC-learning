package com.examp.springmvc.auth.application.ports.input;

import com.examp.springmvc.user.domain.model.User;

public interface LoginInputPort {

    User execute(String username, String password);
}
