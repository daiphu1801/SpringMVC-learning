package com.examp.springmvc.order.domain.model;

import com.examp.springmvc.order.domain.event.OrderCancelledEvent;
import com.examp.springmvc.order.domain.event.OrderPlacedEvent;
import com.examp.springmvc.order.domain.event.OrderStatusChangedEvent;
import com.examp.springmvc.shared.domain.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root của Bounded Context Order.
 * Mọi thay đổi trạng thái đều phải đi qua các phương thức của lớp này
 * để đảm bảo các ràng buộc nghiệp vụ (invariants) luôn được duy trì.
 */
public class Order {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private ShippingAddress shippingAddress;
    private String note;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Constructor dành cho tái tạo từ DB (không kích hoạt event).
     */
    public Order(
            Long id,
            Long userId,
            OrderStatus status,
            List<OrderItem> items,
            BigDecimal totalAmount,
            ShippingAddress shippingAddress,
            String note,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method — Nghiệp vụ đặt hàng mới.
     * Kiểm tra invariant: đơn hàng phải có ít nhất 1 sản phẩm.
     */
    public static Order place(
            Long userId,
            List<OrderItem> items,
            ShippingAddress shippingAddress,
            String note,
            PaymentMethod paymentMethod) {
        if (userId == null) {
            throw new IllegalArgumentException("Người dùng không được để trống");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không được để trống");
        }

        BigDecimal total = items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(
                null,
                userId,
                OrderStatus.PENDING,
                items,
                total,
                shippingAddress,
                note,
                paymentMethod,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());
        order.domainEvents.add(new OrderPlacedEvent(order));
        return order;
    }

    // ===== Các phương thức nghiệp vụ thay đổi trạng thái (Admin) =====

    /**
     * Admin xác nhận đơn hàng.
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Chỉ có thể xác nhận đơn hàng đang ở trạng thái PENDING, hiện tại: " + this.status);
        }
        OrderStatus prev = this.status;
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new OrderStatusChangedEvent(this, prev, this.status));
    }

    /**
     * Admin chuyển sang trạng thái đang vận chuyển.
     */
    public void markShipping() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Chỉ có thể vận chuyển đơn hàng đã xác nhận (CONFIRMED), hiện tại: " + this.status);
        }
        OrderStatus prev = this.status;
        this.status = OrderStatus.SHIPPING;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new OrderStatusChangedEvent(this, prev, this.status));
    }

    /**
     * Admin xác nhận giao hàng thành công.
     */
    public void markDelivered() {
        if (this.status != OrderStatus.SHIPPING) {
            throw new IllegalStateException("Chỉ có thể giao hàng từ trạng thái SHIPPING, hiện tại: " + this.status);
        }
        OrderStatus prev = this.status;
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new OrderStatusChangedEvent(this, prev, this.status));
    }

    /**
     * Huỷ đơn hàng — chỉ cho phép khi còn PENDING hoặc CONFIRMED.
     */
    public void cancel() {
        if (this.status != OrderStatus.PENDING && this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Không thể huỷ đơn hàng đang ở trạng thái " + this.status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new OrderCancelledEvent(this));
    }

    /**
     * Xác nhận thanh toán thành công (dùng cho VietQR sau khi người dùng xác nhận).
     */
    public void markPaymentPaid() {
        if (this.paymentStatus == PaymentStatus.PAID) {
            throw new IllegalStateException("Đơn hàng này đã được thanh toán rồi.");
        }
        this.paymentStatus = PaymentStatus.PAID;
        this.updatedAt = LocalDateTime.now();
        domainEvents.add(new com.examp.springmvc.order.domain.event.OrderPaidEvent(this));
    }

    // ===== Getters =====

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
