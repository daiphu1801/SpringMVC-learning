package com.examp.springmvc.auth.application.ports.input;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;

public interface LoginInputPort {

    AuthenticatedUserDTO execute(String username, String password);
}
