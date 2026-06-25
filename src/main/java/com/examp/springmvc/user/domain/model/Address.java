package com.examp.springmvc.user.domain.model;

import java.io.Serializable;
import java.util.Objects;

public final class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String receiverName;
    private final String receiverPhone;
    private final String province;
    private final String district;
    private final String ward;
    private final String streetDetail;
    private final boolean isDefault;

    public Address(
            Long id,
            String receiverName,
            String receiverPhone,
            String province,
            String district,
            String ward,
            String streetDetail,
            boolean isDefault) {
        validate(receiverName, receiverPhone, province, district, ward, streetDetail);
        this.id = id;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.streetDetail = streetDetail;
        this.isDefault = isDefault;
    }

    private void validate(
            String receiverName,
            String receiverPhone,
            String province,
            String district,
            String ward,
            String streetDetail) {
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người nhận không được để trống");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!receiverPhone.matches("^[0-9]{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải từ 9 đến 11 chữ số)");
        }
        if (province == null || province.trim().isEmpty()) {
            throw new IllegalArgumentException("Tỉnh/Thành phố không được để trống");
        }
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("Quận/Huyện không được để trống");
        }
        if (ward == null || ward.trim().isEmpty()) {
            throw new IllegalArgumentException("Phường/Xã không được để trống");
        }
        if (streetDetail == null || streetDetail.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ chi tiết không được để trống");
        }
    }

    public Long getId() {
        return id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public String getWard() {
        return ward;
    }

    public String getStreetDetail() {
        return streetDetail;
    }

    public boolean isDefault() {
        return isDefault;
    }

    // Helper method to create a copy with a different isDefault value
    public Address withDefault(boolean isDefault) {
        return new Address(
                this.id,
                this.receiverName,
                this.receiverPhone,
                this.province,
                this.district,
                this.ward,
                this.streetDetail,
                isDefault);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return isDefault == address.isDefault
                && Objects.equals(id, address.id)
                && Objects.equals(receiverName, address.receiverName)
                && Objects.equals(receiverPhone, address.receiverPhone)
                && Objects.equals(province, address.province)
                && Objects.equals(district, address.district)
                && Objects.equals(ward, address.ward)
                && Objects.equals(streetDetail, address.streetDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiverName, receiverPhone, province, district, ward, streetDetail, isDefault);
    }
}
