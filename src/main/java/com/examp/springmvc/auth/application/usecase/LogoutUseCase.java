package com.examp.springmvc.auth.application.usecase;

import com.examp.springmvc.auth.application.ports.input.LogoutInputPort;
import org.springframework.stereotype.Component;

@Component
public class LogoutUseCase implements LogoutInputPort {

    @Override
    public void execute() {
        // Thực hiện các logic bổ sung khi đăng xuất nếu cần (ví dụ: audit log)
    }
}
