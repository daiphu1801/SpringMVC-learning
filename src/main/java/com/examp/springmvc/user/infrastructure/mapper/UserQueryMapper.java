package com.examp.springmvc.user.infrastructure.mapper;

import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserQueryMapper {

    List<UserDbEntity> findAll();

    List<UserDbEntity> findPaged(@Param("offset") int offset, @Param("limit") int limit);

    long count();

    UserDbEntity findById(Long id);

    UserDbEntity findByUsername(String username);
}
