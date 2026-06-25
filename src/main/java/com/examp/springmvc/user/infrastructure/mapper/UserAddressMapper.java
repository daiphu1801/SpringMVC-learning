package com.examp.springmvc.user.infrastructure.mapper;

import com.examp.springmvc.user.infrastructure.persistence.UserAddressDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserAddressMapper {

    List<UserAddressDbEntity> findByUserId(@Param("userId") Long userId);

    void insert(UserAddressDbEntity addressEntity);

    void update(UserAddressDbEntity addressEntity);

    void deleteById(@Param("id") Long id);

    void deleteByUserIdExceptIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}
