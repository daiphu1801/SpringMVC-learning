package com.examp.springmvc.user.application.address.command;

public record AddAddressCommand(
        Long userId,
        String receiverName,
        String receiverPhone,
        String province,
        String district,
        String ward,
        String streetDetail,
        boolean isDefault) {}
