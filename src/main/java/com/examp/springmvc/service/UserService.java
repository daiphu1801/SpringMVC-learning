package com.examp.springmvc.service;

import com.examp.springmvc.mapper.UserMapper;
import com.examp.springmvc.model.User;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {

        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userMapper.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy user với id: " + id);
        }

        return user;
    }

    @Transactional
    public void create(User user) {
        validateUser(user);

        User existingUser = userMapper.findByUsername(user.getUsername());

        if (existingUser != null) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        if (user.getStatus() == null || user.getStatus().isBlank()) {

            user.setStatus("ACTIVE");
        }

        int affectedRows = userMapper.insert(user);

        if (affectedRows != 1) {
            throw new IllegalStateException("Không thể tạo user");
        }
    }

    @Transactional
    public void update(User user) {
        validateUser(user);

        if (user.getId() == null) {
            throw new IllegalArgumentException("ID không được để trống");
        }

        findById(user.getId());

        int affectedRows = userMapper.update(user);

        if (affectedRows != 1) {
            throw new IllegalStateException("Không thể cập nhật user");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id);

        int affectedRows = userMapper.deleteById(id);

        if (affectedRows != 1) {
            throw new IllegalStateException("Không thể xóa user");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {

            throw new IllegalArgumentException("Username không được để trống");
        }

        if (user.getFullName() == null || user.getFullName().isBlank()) {

            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {

            throw new IllegalArgumentException("Email không được để trống");
        }
    }
}
