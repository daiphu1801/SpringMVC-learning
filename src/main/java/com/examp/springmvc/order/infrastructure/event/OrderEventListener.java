package com.examp.springmvc.order.infrastructure.event;

import com.examp.springmvc.order.domain.event.OrderCancelledEvent;
import com.examp.springmvc.order.domain.event.OrderPlacedEvent;
import com.examp.springmvc.order.domain.event.OrderStatusChangedEvent;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.ports.output.NotificationPort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEventListener.class);

    private final NotificationPort notificationPort;
    private final FindUserByIdInputPort findUserByIdInputPort;

    public OrderEventListener(NotificationPort notificationPort, FindUserByIdInputPort findUserByIdInputPort) {
        this.notificationPort = notificationPort;
        this.findUserByIdInputPort = findUserByIdInputPort;
    }

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        LOG.info(
                "[ORDER] Đơn hàng mới được đặt thành công - ID: {}, User: {}, Tổng tiền: {}",
                event.getOrder().getId(),
                event.getOrder().getUserId(),
                event.getOrder().getTotalAmount());

        try {
            UserDTO user = findUserByIdInputPort.execute(event.getOrder().getUserId());
            if (user != null && user.getEmail() != null) {
                notificationPort.sendOrderPlacedNotification(event.getOrder(), user.getEmail());
            }
        } catch (Exception ex) {
            LOG.error("[ORDER] Lỗi khi gửi email xác nhận đặt hàng: {}", ex.getMessage());
        }
    }

    @EventListener
    public void onOrderCancelled(OrderCancelledEvent event) {
        LOG.info(
                "[ORDER] Đơn hàng bị huỷ - ID: {}, User: {}",
                event.getOrder().getId(),
                event.getOrder().getUserId());
    }

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        LOG.info(
                "[ORDER] Trạng thái đơn hàng thay đổi - ID: {}, {} → {}",
                event.getOrder().getId(),
                event.getPreviousStatus(),
                event.getNewStatus());

        if (event.getNewStatus() == OrderStatus.DELIVERED) {
            try {
                UserDTO user = findUserByIdInputPort.execute(event.getOrder().getUserId());
                if (user != null && user.getEmail() != null) {
                    notificationPort.sendDeliverySuccess(event.getOrder(), user.getEmail());
                }
            } catch (Exception ex) {
                LOG.error("[ORDER] Lỗi khi gửi email giao hàng thành công: {}", ex.getMessage());
            }
        }
    }
}
