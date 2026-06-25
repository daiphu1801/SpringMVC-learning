package com.examp.springmvc.user.infrastructure.persistence.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    List<UserDbEntity> findAll();

    UserDbEntity findById(@Param("id") Long id);

    UserDbEntity findByUsername(@Param("username") String username);

    int insert(UserDbEntity user);

    int update(UserDbEntity user);

    int deleteById(@Param("id") Long id);
}
