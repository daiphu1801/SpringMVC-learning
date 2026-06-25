package com.examp.springmvc.user.domain.ports.output;

import com.examp.springmvc.user.domain.model.User;
import java.util.Optional;

public interface UserPersistencePort {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    void save(User user);

    void deleteById(Long id);
}
