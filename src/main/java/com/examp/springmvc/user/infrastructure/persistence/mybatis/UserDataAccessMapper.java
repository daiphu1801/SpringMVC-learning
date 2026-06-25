package com.examp.springmvc.user.infrastructure.persistence.mybatis;

import com.examp.springmvc.user.domain.model.User;

public class UserDataAccessMapper {

    public static User toDomain(UserDbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static UserDbEntity toDbEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDbEntity(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
