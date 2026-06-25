package com.examp.springmvc.user.infrastructure.mapper;

import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.util.List;

public interface UserQueryMapper {

    List<UserDbEntity> findAll();

    UserDbEntity findById(Long id);

    UserDbEntity findByUsername(String username);
}
