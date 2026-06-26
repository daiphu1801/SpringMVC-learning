package com.examp.springmvc.order.presentation;

import com.examp.springmvc.order.application.command.UpdateOrderStatusCommand;
import com.examp.springmvc.order.application.command.UpdateOrderStatusUseCase;
import com.examp.springmvc.order.application.query.FindAllOrdersUseCase;
import com.examp.springmvc.order.application.query.FindOrderByIdUseCase;
import com.examp.springmvc.order.application.query.OrderDTO;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final FindAllOrdersUseCase findAllOrdersUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public AdminOrderController(
            FindAllOrdersUseCase findAllOrdersUseCase,
            FindOrderByIdUseCase findOrderByIdUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.findAllOrdersUseCase = findAllOrdersUseCase;
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @GetMapping
    public String listAllOrders(Model model) {
        List<OrderDTO> orders = findAllOrdersUseCase.execute();
        model.addAttribute("orders", orders);
        return "order/admin-order-list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model) {
        OrderDTO order = findOrderByIdUseCase.execute(id);
        model.addAttribute("order", order);
        return "order/admin-order-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("action") String action, Model model) {
        try {
            updateOrderStatusUseCase.execute(new UpdateOrderStatusCommand(id, action));
            return "redirect:/admin/orders/" + id;
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            OrderDTO order = findOrderByIdUseCase.execute(id);
            model.addAttribute("order", order);
            return "order/admin-order-detail";
        }
    }
}
