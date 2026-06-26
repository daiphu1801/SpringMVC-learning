package com.examp.springmvc.order.infrastructure.mapper;

import com.examp.springmvc.order.infrastructure.persistence.OrderDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    void insert(OrderDbEntity entity);

    void update(OrderDbEntity entity);

    OrderDbEntity findById(Long id);

    List<OrderDbEntity> findByUserId(Long userId);

    List<OrderDbEntity> findAll();

    void deleteById(Long id);
}
