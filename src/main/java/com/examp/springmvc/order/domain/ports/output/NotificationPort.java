package com.examp.springmvc.order.domain.ports.output;

import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import java.math.BigDecimal;
import java.util.List;

/** Output Port: gửi thông báo / email cho người dùng liên quan đến đơn hàng. */
public interface NotificationPort {

    /**
     * Gửi email xác nhận đặt hàng thành công.
     *
     * @param orderId ID đơn hàng
     * @param items danh sách sản phẩm trong đơn hàng
     * @param paymentMethod phương thức thanh toán
     * @param totalAmount tổng giá trị đơn hàng
     * @param shippingAddress địa chỉ giao hàng
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendOrderPlacedNotification(
            Long orderId,
            List<OrderItem> items,
            PaymentMethod paymentMethod,
            BigDecimal totalAmount,
            ShippingAddress shippingAddress,
            String recipientEmail);

    /**
     * Gửi email xác nhận thanh toán VietQR thành công.
     *
     * @param orderId ID đơn hàng
     * @param totalAmount tổng giá trị đơn hàng
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendPaymentConfirmation(Long orderId, BigDecimal totalAmount, String recipientEmail);

    /**
     * Gửi email thông báo đơn hàng đã giao thành công.
     *
     * @param orderId ID đơn hàng
     * @param shippingAddress địa chỉ giao hàng
     * @param recipientEmail địa chỉ email người nhận
     */
    void sendDeliverySuccess(Long orderId, ShippingAddress shippingAddress, String recipientEmail);
}
