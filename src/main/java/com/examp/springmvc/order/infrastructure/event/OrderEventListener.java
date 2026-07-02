package com.examp.springmvc.order.infrastructure.event;

import com.examp.springmvc.order.domain.event.OrderCancelledEvent;
import com.examp.springmvc.order.domain.event.OrderPaidEvent;
import com.examp.springmvc.order.domain.event.OrderPlacedEvent;
import com.examp.springmvc.order.domain.event.OrderStatusChangedEvent;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.ports.output.NotificationPort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEventListener.class);

    private final NotificationPort notificationPort;
    private final FindUserByIdInputPort findUserByIdInputPort;

    public OrderEventListener(NotificationPort notificationPort, FindUserByIdInputPort findUserByIdInputPort) {
        this.notificationPort = notificationPort;
        this.findUserByIdInputPort = findUserByIdInputPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void onOrderPlaced(OrderPlacedEvent event) {
        LOG.info(
                "[ORDER] Đơn hàng mới được đặt thành công - ID: {}, User: {}, Tổng tiền: {}",
                event.getOrderId(),
                event.getUserId(),
                event.getTotalAmount());

        try {
            UserDTO user = findUserByIdInputPort.execute(event.getUserId());
            if (user != null && user.getEmail() != null) {
                notificationPort.sendOrderPlacedNotification(
                        event.getOrderId(),
                        event.getItems(),
                        event.getPaymentMethod(),
                        event.getTotalAmount(),
                        event.getShippingAddress(),
                        user.getEmail());
            }
        } catch (Exception ex) {
            LOG.error("[ORDER] Lỗi khi gửi email xác nhận đặt hàng: {}", ex.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void onOrderCancelled(OrderCancelledEvent event) {
        LOG.info("[ORDER] Đơn hàng bị huỷ - ID: {}, User: {}", event.getOrderId(), event.getUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        LOG.info(
                "[ORDER] Trạng thái đơn hàng thay đổi - ID: {}, {} → {}",
                event.getOrderId(),
                event.getPreviousStatus(),
                event.getNewStatus());

        if (event.getNewStatus() == OrderStatus.DELIVERED) {
            try {
                UserDTO user = findUserByIdInputPort.execute(event.getUserId());
                if (user != null && user.getEmail() != null) {
                    notificationPort.sendDeliverySuccess(
                            event.getOrderId(), event.getShippingAddress(), user.getEmail());
                }
            } catch (Exception ex) {
                LOG.error("[ORDER] Lỗi khi gửi email giao hàng thành công: {}", ex.getMessage());
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void onOrderPaid(OrderPaidEvent event) {
        LOG.info(
                "[ORDER] Đơn hàng đã được thanh toán thành công - ID: {}, User: {}, Tổng tiền: {}",
                event.getOrderId(),
                event.getUserId(),
                event.getTotalAmount());

        try {
            UserDTO user = findUserByIdInputPort.execute(event.getUserId());
            if (user != null && user.getEmail() != null) {
                notificationPort.sendPaymentConfirmation(event.getOrderId(), event.getTotalAmount(), user.getEmail());
            }
        } catch (Exception ex) {
            LOG.error("[ORDER] Lỗi khi gửi email xác nhận thanh toán: {}", ex.getMessage());
        }
    }
}
