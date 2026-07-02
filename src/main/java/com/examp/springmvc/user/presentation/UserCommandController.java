package com.examp.springmvc.user.presentation;

import com.examp.springmvc.user.application.usermanagement.command.CreateUserCommand;
import com.examp.springmvc.user.application.usermanagement.command.CreateUserInputPort;
import com.examp.springmvc.user.application.usermanagement.command.DeleteUserInputPort;
import com.examp.springmvc.user.application.usermanagement.command.UpdateUserCommand;
import com.examp.springmvc.user.application.usermanagement.command.UpdateUserInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserCommandController {

    private static final Logger LOG = LoggerFactory.getLogger(UserCommandController.class);

    private final CreateUserInputPort createUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final DeleteUserInputPort deleteUserInputPort;

    public UserCommandController(
            CreateUserInputPort createUserInputPort,
            UpdateUserInputPort updateUserInputPort,
            DeleteUserInputPort deleteUserInputPort) {
        this.createUserInputPort = createUserInputPort;
        this.updateUserInputPort = updateUserInputPort;
        this.deleteUserInputPort = deleteUserInputPort;
    }

    @PostMapping
    public String create(@ModelAttribute("user") CreateUserCommand command, Model model) {
        try {
            createUserInputPort.execute(command);
            return "redirect:/users";
        } catch (Exception e) {
            LOG.error("Error creating user: ", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", command);
            return "user/form";
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable("id") Long id,
            @ModelAttribute("user") UpdateUserCommand command,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Temporary disabled during security testing to prevent account corruption
            LOG.info("Bypassed updating user with ID {} (Disabled for security testing)", id);
            redirectAttributes.addFlashAttribute(
                    "success", "Tính năng cập nhật thông tin đã tạm thời được vô hiệu hóa để phục vụ thử nghiệm.");
            return "redirect:/users";
        } catch (Exception e) {
            LOG.error("Error updating user with ID {}: ", id, e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", command);
            return "user/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Temporary disabled during security testing to prevent account loss
            LOG.info("Bypassed deleting user with ID {} (Disabled for security testing)", id);
            redirectAttributes.addFlashAttribute(
                    "success", "Tính năng xóa tài khoản đã tạm thời được vô hiệu hóa để phục vụ thử nghiệm.");
        } catch (Exception e) {
            LOG.error("Error deleting user with ID {}: ", id, e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
