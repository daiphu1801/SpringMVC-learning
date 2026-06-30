package com.examp.springmvc.order.domain.model;

import java.util.Objects;

public final class ShippingAddress {

    private final String receiverName;
    private final String receiverPhone;
    private final String fullAddress;

    public ShippingAddress(String receiverName, String receiverPhone, String fullAddress) {
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người nhận không được để trống");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!receiverPhone.trim().matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải từ 9 đến 11 chữ số)");
        }
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
        }
        this.receiverName = receiverName.trim();
        this.receiverPhone = receiverPhone.trim();
        this.fullAddress = fullAddress.trim();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    /**
     * Factory method: tạo ShippingAddress từ các thành phần địa chỉ.
     */
    public static ShippingAddress of(
            String receiverName,
            String receiverPhone,
            String streetDetail,
            String ward,
            String district,
            String province) {
        String full = streetDetail + ", " + ward + ", " + district + ", " + province;
        return new ShippingAddress(receiverName, receiverPhone, full);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShippingAddress that = (ShippingAddress) o;
        return Objects.equals(receiverName, that.receiverName)
                && Objects.equals(receiverPhone, that.receiverPhone)
                && Objects.equals(fullAddress, that.fullAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverName, receiverPhone, fullAddress);
    }
}
