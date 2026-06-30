package com.examp.springmvc.user.application.address.query;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetUserAddressesUseCase implements GetUserAddressesInputPort {

    private final UserAddressQueryPort userAddressQueryPort;

    public GetUserAddressesUseCase(UserAddressQueryPort userAddressQueryPort) {
        this.userAddressQueryPort = userAddressQueryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> execute(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được null");
        }
        return userAddressQueryPort.findByUserId(userId);
    }
}
