package com.examp.springmvc.order.application.query;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindOrderByIdUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindOrderByIdUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional(readOnly = true)
    public OrderDTO execute(Long orderId) {
        Order order = orderPersistencePort
                .findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));
        return OrderDTO.fromDomain(order);
    }
}
