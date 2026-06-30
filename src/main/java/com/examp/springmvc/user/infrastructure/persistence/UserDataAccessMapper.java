package com.examp.springmvc.user.infrastructure.persistence;

import com.examp.springmvc.user.domain.model.Address;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDataAccessMapper {

    public User toDomain(UserDbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                entity.getEmail() != null ? new Email(entity.getEmail()) : null,
                entity.getPhone(),
                entity.getStatus() != null
                        ? com.examp.springmvc.user.domain.model.UserStatus.valueOf(entity.getStatus())
                        : null,
                entity.getPassword() != null ? Password.fromHashed(entity.getPassword()) : null,
                entity.getRole() != null
                        ? com.examp.springmvc.user.domain.model.UserRole.valueOf(entity.getRole())
                        : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public UserDbEntity toDbEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDbEntity(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getPhone(),
                user.getStatus() != null ? user.getStatus().name() : null,
                user.getPassword() != null ? user.getPassword().getHashedValue() : null,
                user.getRole() != null ? user.getRole().name() : null,
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public Address toAddressDomain(UserAddressDbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Address(
                entity.getId(),
                entity.getReceiverName(),
                entity.getReceiverPhone(),
                entity.getProvince(),
                entity.getDistrict(),
                entity.getWard(),
                entity.getStreetDetail(),
                entity.isDefault());
    }

    public UserAddressDbEntity toAddressDbEntity(Address address, Long userId) {
        if (address == null) {
            return null;
        }
        return new UserAddressDbEntity(
                address.getId(),
                userId,
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getProvince(),
                address.getDistrict(),
                address.getWard(),
                address.getStreetDetail(),
                address.isDefault());
    }
}
