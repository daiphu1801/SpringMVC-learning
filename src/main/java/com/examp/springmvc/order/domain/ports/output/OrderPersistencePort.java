package com.examp.springmvc.order.domain.ports.output;

import com.examp.springmvc.order.domain.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderPersistencePort {

    Order save(Order order);

    Order saveViaProcedure(Order order, String itemsCsv);

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findAll();

    void deleteById(Long id);
}
