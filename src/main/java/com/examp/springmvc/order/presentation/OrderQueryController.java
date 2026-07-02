package com.examp.springmvc.order.presentation;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.order.application.query.FindOrderByIdUseCase;
import com.examp.springmvc.order.application.query.FindOrdersByUserUseCase;
import com.examp.springmvc.order.application.query.OrderDTO;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/orders")
public class OrderQueryController {

    private final FindOrdersByUserUseCase findOrdersByUserUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;

    @Value("${vietqr.bank-code}")
    private String bankCode;

    @Value("${vietqr.account-number}")
    private String accountNumber;

    @Value("${vietqr.account-name}")
    private String accountName;

    public OrderQueryController(
            FindOrdersByUserUseCase findOrdersByUserUseCase, FindOrderByIdUseCase findOrderByIdUseCase) {
        this.findOrdersByUserUseCase = findOrdersByUserUseCase;
        this.findOrderByIdUseCase = findOrderByIdUseCase;
    }

    @GetMapping
    public String listOrders(@SessionAttribute("currentUser") AuthenticatedUserDTO currentUser, Model model) {
        List<OrderDTO> orders = findOrdersByUserUseCase.execute(currentUser.getId());
        model.addAttribute("orders", orders);
        return "order/order-history";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            @PathVariable("id") Long id,
            @SessionAttribute("currentUser") AuthenticatedUserDTO currentUser,
            Model model) {
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
            @SessionAttribute("currentUser") AuthenticatedUserDTO currentUser,
            Model model) {
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "order/checkout";
    }

    @GetMapping("/{id}/payment")
    public String showPayment(
            @PathVariable("id") Long id,
            @SessionAttribute("currentUser") AuthenticatedUserDTO currentUser,
            Model model) {
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
}
