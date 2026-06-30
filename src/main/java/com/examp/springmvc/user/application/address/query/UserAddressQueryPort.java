package com.examp.springmvc.user.application.address.query;

import java.util.List;

public interface UserAddressQueryPort {
    List<AddressDTO> findByUserId(Long userId);
}
