package com.examp.springmvc.order.application.command;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CancelOrderUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional
    public void execute(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderPersistencePort
                .findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!isAdmin && !order.getUserId().equals(requestingUserId)) {
            throw new IllegalStateException("Bạn không có quyền huỷ đơn hàng này");
        }

        // Gọi nghiệp vụ huỷ đơn trong Domain — invariant kiểm tra tại đây
        order.cancel();

        orderPersistencePort.save(order);
    }
}
