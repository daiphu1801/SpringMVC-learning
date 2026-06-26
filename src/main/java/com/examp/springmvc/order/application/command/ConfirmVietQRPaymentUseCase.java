package com.examp.springmvc.order.application.command;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.ports.output.NotificationPort;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Use case: Người dùng xác nhận đã thanh toán đơn hàng qua quét mã QR (VietQR). */
@Service
public class ConfirmVietQRPaymentUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final NotificationPort notificationPort;
    private final FindUserByIdInputPort findUserByIdInputPort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ConfirmVietQRPaymentUseCase(
            OrderPersistencePort orderPersistencePort,
            NotificationPort notificationPort,
            FindUserByIdInputPort findUserByIdInputPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.notificationPort = notificationPort;
        this.findUserByIdInputPort = findUserByIdInputPort;
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

        // Gửi email thông báo thanh toán thành công
        try {
            UserDTO user = findUserByIdInputPort.execute(userId);
            if (user != null && user.getEmail() != null) {
                notificationPort.sendPaymentConfirmation(order, user.getEmail());
            }
        } catch (Exception ex) {
            // Không làm gián đoạn transaction nếu gửi email lỗi
            org.slf4j.LoggerFactory.getLogger(ConfirmVietQRPaymentUseCase.class)
                    .error("Lỗi gửi email xác nhận thanh toán VietQR: {}", ex.getMessage());
        }
    }
}
