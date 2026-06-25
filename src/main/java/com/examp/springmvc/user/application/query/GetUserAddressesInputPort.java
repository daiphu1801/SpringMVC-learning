package com.examp.springmvc.user.application.query;

import java.util.List;

public interface GetUserAddressesInputPort {
    List<AddressDTO> execute(Long userId);
}
