package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userFindAllUsersUseCase")
public class FindAllUsersUseCase implements FindAllUsersInputPort {

    private final UserQueryMapper userQueryMapper;

    public FindAllUsersUseCase(UserQueryMapper userQueryMapper) {
        this.userQueryMapper = userQueryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> execute() {
        List<UserDbEntity> entities = userQueryMapper.findAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
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
