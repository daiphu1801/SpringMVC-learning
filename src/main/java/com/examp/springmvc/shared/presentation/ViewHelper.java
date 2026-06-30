package com.examp.springmvc.shared.presentation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ViewHelper {

    private ViewHelper() {}

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    public static String formatPaymentMethod(String paymentMethod) {
        if ("VIETQR".equals(paymentMethod)) {
            return "Chuyển khoản VietQR";
        } else if ("CASH".equals(paymentMethod)) {
            return "Tiền mặt khi nhận hàng";
        }
        return paymentMethod != null ? paymentMethod : "";
    }

    public static String formatPaymentStatus(String paymentStatus) {
        if ("PAID".equals(paymentStatus)) {
            return "Đã thanh toán";
        } else if ("PENDING".equals(paymentStatus)) {
            return "Chờ thanh toán";
        }
        return paymentStatus != null ? paymentStatus : "";
    }
}
