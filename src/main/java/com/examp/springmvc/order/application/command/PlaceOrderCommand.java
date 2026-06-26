package com.examp.springmvc.order.application.command;

import com.examp.springmvc.order.domain.model.PaymentMethod;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

public class PlaceOrderCommand {

    private final Long userId;
    private final List<OrderItemRequest> items;
    private final String receiverName;
    private final String receiverPhone;
    private final String streetDetail;
    private final String ward;
    private final String district;
    private final String province;
    private final String note;
    private final PaymentMethod paymentMethod;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PlaceOrderCommand(
            Long userId,
            List<OrderItemRequest> items,
            String receiverName,
            String receiverPhone,
            String streetDetail,
            String ward,
            String district,
            String province,
            String note,
            PaymentMethod paymentMethod) {
        this.userId = userId;
        this.items = items;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.streetDetail = streetDetail;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.note = note;
        this.paymentMethod = paymentMethod;
    }

    public Long getUserId() {
        return userId;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public List<OrderItemRequest> getItems() {
        return items;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getStreetDetail() {
        return streetDetail;
    }

    public String getWard() {
        return ward;
    }

    public String getDistrict() {
        return district;
    }

    public String getProvince() {
        return province;
    }

    public String getNote() {
        return note;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * DTO nội bộ đại diện cho mỗi dòng yêu cầu trong đơn hàng.
     */
    public static class OrderItemRequest {
        private final Long productId;
        private final int quantity;

        public OrderItemRequest(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
