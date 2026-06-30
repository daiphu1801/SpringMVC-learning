package com.examp.springmvc.user.infrastructure.persistence;

import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import com.examp.springmvc.user.application.usermanagement.query.UserQueryPort;
import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserQueryAdapter implements UserQueryPort {

    private final UserQueryMapper userQueryMapper;

    public UserQueryAdapter(UserQueryMapper userQueryMapper) {
        this.userQueryMapper = userQueryMapper;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserDbEntity> entities = userQueryMapper.findAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        UserDbEntity entity = userQueryMapper.findById(id);
        return Optional.ofNullable(toDTO(entity));
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
