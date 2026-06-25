package com.examp.springmvc.user.infrastructure.mapper;

import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;

public interface UserCommandMapper {

    void insert(UserDbEntity userDbEntity);

    void update(UserDbEntity userDbEntity);

    void deleteById(Long id);
}
