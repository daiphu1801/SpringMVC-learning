package com.examp.springmvc.user.application.address.command;

public interface RemoveAddressInputPort {
    void execute(Long userId, Long addressId);
}
