package com.examp.springmvc.user.application.address.query;

import java.util.List;

public interface GetUserAddressesInputPort {
    List<AddressDTO> execute(Long userId);
}
