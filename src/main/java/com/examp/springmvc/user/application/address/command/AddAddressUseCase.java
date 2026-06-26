package com.examp.springmvc.user.application.address.command;

import com.examp.springmvc.user.domain.model.Address;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddAddressUseCase implements AddAddressInputPort {

    private final UserPersistencePort userPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AddAddressUseCase(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional
    public void execute(AddAddressCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command không được null");
        }

        User user = userPersistencePort
                .findById(command.userId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + command.userId()));

        Address address = new Address(
                null,
                command.receiverName(),
                command.receiverPhone(),
                command.province(),
                command.district(),
                command.ward(),
                command.streetDetail(),
                command.isDefault());

        user.addAddress(address);
        userPersistencePort.save(user);
    }
}
