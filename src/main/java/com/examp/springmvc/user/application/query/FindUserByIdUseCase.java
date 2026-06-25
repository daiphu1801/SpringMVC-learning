package com.examp.springmvc.user.application.query;

import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userFindUserByIdUseCase")
public class FindUserByIdUseCase implements FindUserByIdInputPort {

    private final UserQueryMapper userQueryMapper;

    public FindUserByIdUseCase(UserQueryMapper userQueryMapper) {
        this.userQueryMapper = userQueryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }
        UserDbEntity entity = userQueryMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Không tìm thấy user với id: " + id);
        }
        return toDTO(entity);
    }

    private UserDTO toDTO(UserDbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
