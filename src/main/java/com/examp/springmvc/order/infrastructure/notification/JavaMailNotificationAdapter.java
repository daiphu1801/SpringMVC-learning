package com.examp.springmvc.order.infrastructure.notification;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.ports.output.NotificationPort;
import java.text.NumberFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/** Infrastructure adapter: gửi email thật qua SMTP dùng Spring JavaMailSender. */
@Component
public class JavaMailNotificationAdapter implements NotificationPort {

    private static final Logger LOG = LoggerFactory.getLogger(JavaMailNotificationAdapter.class);
    private static final NumberFormat VND_FORMAT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromAddress;

    @Value("${mail.from-name}")
    private String fromName;

    public JavaMailNotificationAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOrderPlacedNotification(Order order, String recipientEmail) {
        String subject = "[SpringMVC Shop] \u0110\u1eb7t h\u00e0ng #" + order.getId() + " th\u00e0nh c\u00f4ng!";
        String body = buildOrderPlacedBody(order);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Override
    public void sendPaymentConfirmation(Order order, String recipientEmail) {
        String subject =
                "[SpringMVC Shop] X\u00e1c nh\u1eadn thanh to\u00e1n \u0111\u01a1n h\u00e0ng #" + order.getId();
        String body = buildPaymentConfirmationBody(order);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Override
    public void sendDeliverySuccess(Order order, String recipientEmail) {
        String subject = "[SpringMVC Shop] \u0110\u01a1n h\u00e0ng #" + order.getId()
                + " \u0111\u00e3 giao th\u00e0nh c\u00f4ng!";
        String body = buildDeliverySuccessBody(order);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(fromAddress, fromName);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
            });
            LOG.info("[MAIL] \u0110\u00e3 g\u1eedi email '{}' \u0111\u1ebfn {}", subject, to);
        } catch (MailException ex) {
            LOG.warn("[MAIL] Kh\u00f4ng th\u1ec3 g\u1eedi email \u0111\u1ebfn {}: {}", to, ex.getMessage());
        } catch (Exception ex) {
            LOG.warn("[MAIL] L\u1ed7i khi chu\u1ea9n b\u1ecb email \u0111\u1ebfn {}: {}", to, ex.getMessage());
        }
    }

    private String buildOrderPlacedBody(Order order) {
        StringBuilder items = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            items.append("<tr>")
                    .append("<td style='padding:8px;border-bottom:1px solid #eee;'>")
                    .append(item.getProductName())
                    .append("</td>")
                    .append("<td style='padding:8px;border-bottom:1px solid #eee;text-align:center;'>")
                    .append(item.getQuantity())
                    .append("</td>")
                    .append("<td style='padding:8px;border-bottom:1px solid #eee;text-align:right;'>")
                    .append(VND_FORMAT.format(item.getSubtotal()))
                    .append(" \u0111</td>")
                    .append("</tr>");
        }
        String paymentLabel = "VIETQR".equals(order.getPaymentMethod().name())
                ? "📱 VietQR (Chuyển khoản)"
                : "💵 Tiền mặt khi nhận hàng";
        return emailWrapper(
                "🎉 Đặt hàng thành công!",
                "Cảm ơn bạn đã đặt hàng tại <strong>SpringMVC Shop</strong>. "
                        + "Đơn hàng của bạn đã được tiếp nhận và đang chờ xử lý.",
                "<h3 style='color:#333;margin-top:24px;'>📦 Chi tiết đơn hàng #"
                        + order.getId()
                        + "</h3>"
                        + "<table style='width:100%;border-collapse:collapse;'>"
                        + "<thead><tr style='background:#f5f5f5;'>"
                        + "<th style='padding:8px;text-align:left;'>Sản phẩm</th>"
                        + "<th style='padding:8px;text-align:center;'>Số lượng</th>"
                        + "<th style='padding:8px;text-align:right;'>Thành tiền</th>"
                        + "</tr></thead><tbody>"
                        + items
                        + "</tbody></table>"
                        + "<div style='text-align:right;margin-top:12px;font-size:1.2rem;"
                        + "font-weight:700;color:#2ecc71;'>"
                        + "Tổng cộng: "
                        + VND_FORMAT.format(order.getTotalAmount())
                        + " đ"
                        + "</div>"
                        + "<div style='margin-top:16px;padding:12px;background:#f8f8ff;"
                        + "border-radius:8px;'>"
                        + "<strong>📍 Địa chỉ giao hàng:</strong> "
                        + order.getShippingAddress().getFullAddress()
                        + "<br><strong>💳 Phương thức thanh toán:</strong> "
                        + paymentLabel
                        + "</div>");
    }

    private String buildPaymentConfirmationBody(Order order) {
        return emailWrapper(
                "✅ Thanh toán thành công!",
                "Hệ thống đã xác nhận thanh toán cho đơn hàng <strong>#" + order.getId() + "</strong> của bạn.",
                "<div style='padding:16px;background:#f0fff4;border:1px solid #2ecc71;"
                        + "border-radius:8px;text-align:center;'>"
                        + "<div style='font-size:2rem;margin-bottom:8px;'>🎉</div>"
                        + "<div style='font-size:1.4rem;font-weight:700;color:#27ae60;'>Số tiền: "
                        + VND_FORMAT.format(order.getTotalAmount())
                        + " đ</div>"
                        + "<div style='margin-top:8px;color:#555;'>Phương thức: VietQR (Chuyển khoản)</div>"
                        + "</div>"
                        + "<p style='margin-top:16px;color:#555;'>Trạng thái đơn hàng đã được chuyển sang "
                        + "<strong>Xác nhận</strong>. Chúng tôi sẽ nhanh chóng xử lý và giao hàng đến bạn.</p>");
    }

    private String buildDeliverySuccessBody(Order order) {
        return emailWrapper(
                "🚚 Giao hàng thành công!",
                "Đơn hàng <strong>#" + order.getId() + "</strong> của bạn đã được giao đến nơi.",
                "<div style='padding:16px;background:#fff8e1;border:1px solid #f39c12;"
                        + "border-radius:8px;text-align:center;'>"
                        + "<div style='font-size:2.5rem;margin-bottom:8px;'>🏷️</div>"
                        + "<div style='font-size:1.1rem;font-weight:700;color:#e67e22;'>Giao thành công đến:</div>"
                        + "<div style='margin-top:8px;color:#333;'>"
                        + order.getShippingAddress().getFullAddress()
                        + "</div>"
                        + "</div>"
                        + "<p style='margin-top:16px;color:#555;'>Cảm ơn bạn đã tin tưởng mua sắm tại "
                        + "<strong>SpringMVC Shop</strong>! Nếu có bất kỳ vấn đề nào, hãy liên hệ ngay.</p>");
    }

    private String emailWrapper(String heading, String intro, String content) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>"
                + "<div style='background:linear-gradient(135deg,#7F84FF,#9FA1FF);padding:24px;"
                + "border-radius:12px 12px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:1.5rem;'>"
                + heading
                + "</h1>"
                + "</div>"
                + "<div style='padding:24px;background:#fafafa;border:1px solid #eee;'>"
                + "<p style='color:#333;margin-top:0;'>"
                + intro
                + "</p>"
                + content
                + "</div>"
                + "<div style='background:#f5f5f5;padding:16px;text-align:center;"
                + "border-radius:0 0 12px 12px;border:1px solid #eee;border-top:none;'>"
                + "<p style='color:#999;font-size:0.8rem;margin:0;'>© 2026 SpringMVC Shop &bull; "
                + "Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>"
                + "</div>";
    }
}
