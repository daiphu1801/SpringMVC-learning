package com.examp.springmvc.user.application.command;

public interface RemoveAddressInputPort {
    void execute(Long userId, Long addressId);
}
