package com.examp.springmvc.order.domain.ports.output;

import com.examp.springmvc.order.domain.model.Order;

/** Output Port: gửi thông báo / email cho người dùng liên quan đến đơn hàng. */
public interface NotificationPort {

    /**
     * Gửi email xác nhận đặt hàng thành công.
     *
     * @param order đơn hàng vừa được đặt
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendOrderPlacedNotification(Order order, String recipientEmail);

    /**
     * Gửi email xác nhận thanh toán VietQR thành công.
     *
     * @param order đơn hàng đã được thanh toán
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendPaymentConfirmation(Order order, String recipientEmail);

    /**
     * Gửi email thông báo đơn hàng đã giao thành công.
     *
     * @param order đơn hàng đã giao
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendDeliverySuccess(Order order, String recipientEmail);
}
