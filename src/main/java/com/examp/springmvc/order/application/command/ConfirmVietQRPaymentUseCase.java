package com.examp.springmvc.order.application.command;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Use case: Người dùng xác nhận đã thanh toán đơn hàng qua quét mã QR (VietQR). */
@Service
public class ConfirmVietQRPaymentUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ConfirmVietQRPaymentUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional
    public void execute(Long orderId, Long userId) {
        Order order = orderPersistencePort
                .findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền thanh toán đơn hàng này");
        }

        if (order.getPaymentMethod() != PaymentMethod.VIETQR) {
            throw new IllegalStateException("Đơn hàng này không sử dụng phương thức thanh toán VietQR");
        }

        // Thực hiện nghiệp vụ cập nhật trạng thái đơn hàng và thanh toán
        order.confirm();
        order.markPaymentPaid();

        orderPersistencePort.save(order);
    }
}
