package com.examp.springmvc.mapper;

import com.examp.springmvc.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    List<User> findAll();

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);
}
