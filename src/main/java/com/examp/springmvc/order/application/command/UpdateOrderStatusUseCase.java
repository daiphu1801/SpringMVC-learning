package com.examp.springmvc.order.application.command;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOrderStatusUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UpdateOrderStatusUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional
    public void execute(UpdateOrderStatusCommand command) {
        Order order = orderPersistencePort
                .findById(command.getOrderId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + command.getOrderId()));

        switch (command.getAction()) {
            case CONFIRM:
                order.confirm();
                break;
            case SHIP:
                order.markShipping();
                break;
            case DELIVER:
                order.markDelivered();
                break;
            default:
                throw new IllegalArgumentException("Hành động không hợp lệ: " + command.getAction());
        }

        orderPersistencePort.save(order);
    }
}
