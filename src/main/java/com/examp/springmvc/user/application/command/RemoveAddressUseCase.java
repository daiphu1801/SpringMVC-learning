package com.examp.springmvc.user.application.command;

import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveAddressUseCase implements RemoveAddressInputPort {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public RemoveAddressUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional
    public void execute(Long userId, Long addressId) {
        if (userId == null || addressId == null) {
            throw new IllegalArgumentException("User ID và Address ID không được null");
        }

        User user = userPersistencePort
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        user.removeAddress(addressId);
        userPersistencePort.save(user);
    }
}
