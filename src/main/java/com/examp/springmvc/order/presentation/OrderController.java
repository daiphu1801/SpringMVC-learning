package com.examp.springmvc.order.presentation;

import com.examp.springmvc.order.application.command.CancelOrderUseCase;
import com.examp.springmvc.order.application.command.ConfirmVietQRPaymentUseCase;
import com.examp.springmvc.order.application.command.PlaceOrderCommand;
import com.examp.springmvc.order.application.command.PlaceOrderUseCase;
import com.examp.springmvc.order.application.query.FindOrderByIdUseCase;
import com.examp.springmvc.order.application.query.FindOrdersByUserUseCase;
import com.examp.springmvc.order.application.query.OrderDTO;
import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final FindOrdersByUserUseCase findOrdersByUserUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final ConfirmVietQRPaymentUseCase confirmVietQRPaymentUseCase;

    @Value("${vietqr.bank-code}")
    private String bankCode;

    @Value("${vietqr.account-number}")
    private String accountNumber;

    @Value("${vietqr.account-name}")
    private String accountName;

    public OrderController(
            PlaceOrderUseCase placeOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            FindOrdersByUserUseCase findOrdersByUserUseCase,
            FindOrderByIdUseCase findOrderByIdUseCase,
            ConfirmVietQRPaymentUseCase confirmVietQRPaymentUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.findOrdersByUserUseCase = findOrdersByUserUseCase;
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.confirmVietQRPaymentUseCase = confirmVietQRPaymentUseCase;
    }

    @GetMapping
    public String listOrders(@SessionAttribute("currentUser") User currentUser, Model model) {
        List<OrderDTO> orders = findOrdersByUserUseCase.execute(currentUser.getId());
        model.addAttribute("orders", orders);
        return "order/order-history";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            @PathVariable("id") Long id, @SessionAttribute("currentUser") User currentUser, Model model) {
        OrderDTO order = findOrderByIdUseCase.execute(id);
        if (!order.getUserId().equals(currentUser.getId())
                && currentUser.getRole() != com.examp.springmvc.user.domain.model.UserRole.ADMIN) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "order/order-detail";
    }

    @GetMapping("/checkout")
    public String showCheckout(
            @SessionAttribute(name = "cart", required = false) Map<Long, Integer> cart,
            @SessionAttribute("currentUser") User currentUser,
            Model model) {
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "order/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("streetDetail") String streetDetail,
            @RequestParam("ward") String ward,
            @RequestParam("district") String district,
            @RequestParam("province") String province,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("paymentMethod") String paymentMethodStr,
            @SessionAttribute("currentUser") User currentUser,
            jakarta.servlet.http.HttpSession session,
            Model model) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
        } catch (Exception e) {
            paymentMethod = PaymentMethod.CASH;
        }

        List<PlaceOrderCommand.OrderItemRequest> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            items.add(new PlaceOrderCommand.OrderItemRequest(entry.getKey(), entry.getValue()));
        }

        PlaceOrderCommand command = new PlaceOrderCommand(
                currentUser.getId(),
                items,
                receiverName,
                receiverPhone,
                streetDetail,
                ward,
                district,
                province,
                note,
                paymentMethod);

        try {
            Order order = placeOrderUseCase.execute(command);
            session.removeAttribute("cart");
            if (order.getPaymentMethod() == PaymentMethod.VIETQR) {
                return "redirect:/orders/" + order.getId() + "/payment";
            }
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "order/checkout";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            @PathVariable("id") Long id, @SessionAttribute("currentUser") User currentUser, Model model) {
        try {
            cancelOrderUseCase.execute(id, currentUser.getId());
            return "redirect:/orders/" + id;
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            OrderDTO order = findOrderByIdUseCase.execute(id);
            model.addAttribute("order", order);
            return "order/order-detail";
        }
    }

    @GetMapping("/{id}/payment")
    public String showPayment(
            @PathVariable("id") Long id, @SessionAttribute("currentUser") User currentUser, Model model) {
        OrderDTO order = findOrderByIdUseCase.execute(id);
        if (!order.getUserId().equals(currentUser.getId())) {
            return "redirect:/orders";
        }
        if (!"VIETQR".equals(order.getPaymentMethod())) {
            return "redirect:/orders/" + id;
        }
        if ("PAID".equals(order.getPaymentStatus())) {
            return "redirect:/orders/" + id + "?paymentAlreadyPaid=true";
        }

        model.addAttribute("order", order);
        model.addAttribute("bankCode", bankCode);
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("accountName", accountName);
        return "order/payment-vietqr";
    }

    @PostMapping("/{id}/payment/confirm")
    public String confirmPayment(
            @PathVariable("id") Long id, @SessionAttribute("currentUser") User currentUser, Model model) {
        try {
            confirmVietQRPaymentUseCase.execute(id, currentUser.getId());
            return "redirect:/orders/" + id + "?paymentSuccess=true";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            OrderDTO order = findOrderByIdUseCase.execute(id);
            model.addAttribute("order", order);
            model.addAttribute("bankCode", bankCode);
            model.addAttribute("accountNumber", accountNumber);
            model.addAttribute("accountName", accountName);
            return "order/payment-vietqr";
        }
    }
}
