package com.examp.springmvc.order.presentation;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.order.application.command.CancelOrderUseCase;
import com.examp.springmvc.order.application.command.OrderStatusAction;
import com.examp.springmvc.order.application.command.UpdateOrderStatusCommand;
import com.examp.springmvc.order.application.command.UpdateOrderStatusUseCase;
import com.examp.springmvc.order.application.query.FindOrderByIdUseCase;
import com.examp.springmvc.order.application.query.OrderDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderCommandController {

    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public AdminOrderCommandController(
            FindOrderByIdUseCase findOrderByIdUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase,
            CancelOrderUseCase cancelOrderUseCase) {
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("action") String action,
            @SessionAttribute("currentUser") AuthenticatedUserDTO currentUser,
            Model model) {
        try {
            if ("cancel".equalsIgnoreCase(action)) {
                cancelOrderUseCase.execute(id, currentUser.getId(), true);
            } else {
                OrderStatusAction statusAction = OrderStatusAction.valueOf(action.toUpperCase());
                updateOrderStatusUseCase.execute(new UpdateOrderStatusCommand(id, statusAction));
            }
            return "redirect:/admin/orders/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            OrderDTO order = findOrderByIdUseCase.execute(id);
            model.addAttribute("order", order);
            return "order/admin-order-detail";
        }
    }
}
