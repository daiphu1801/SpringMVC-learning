package com.examp.springmvc.user.infrastructure.persistence.mybatis;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserMapper userMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UserPersistenceAdapter(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll().stream().map(UserDataAccessMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.findById(id)).map(UserDataAccessMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username)).map(UserDataAccessMapper::toDomain);
    }

    @Override
    public void save(User user) {
        UserDbEntity entity = UserDataAccessMapper.toDbEntity(user);
        if (user.getId() == null) {
            int affectedRows = userMapper.insert(entity);
            if (affectedRows != 1) {
                throw new IllegalStateException("Không thể tạo user");
            }
            user.setId(entity.getId());
        } else {
            int affectedRows = userMapper.update(entity);
            if (affectedRows != 1) {
                throw new IllegalStateException("Không thể cập nhật user");
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        int affectedRows = userMapper.deleteById(id);
        if (affectedRows != 1) {
            throw new IllegalStateException("Không thể xóa user");
        }
    }
}
