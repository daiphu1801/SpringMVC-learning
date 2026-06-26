package com.examp.springmvc.order.infrastructure.mapper;

import com.examp.springmvc.order.infrastructure.persistence.OrderItemDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItemDbEntity entity);

    List<OrderItemDbEntity> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}
