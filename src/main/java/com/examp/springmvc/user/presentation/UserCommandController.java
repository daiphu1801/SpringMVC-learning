package com.examp.springmvc.user.presentation;

import com.examp.springmvc.user.application.command.CreateUserCommand;
import com.examp.springmvc.user.application.command.CreateUserInputPort;
import com.examp.springmvc.user.application.command.DeleteUserCommand;
import com.examp.springmvc.user.application.command.DeleteUserInputPort;
import com.examp.springmvc.user.application.command.UpdateUserCommand;
import com.examp.springmvc.user.application.command.UpdateUserInputPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserCommandController {

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
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", command);
            return "user/form";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("user") UpdateUserCommand command, Model model) {
        try {
            command.setId(id);
            updateUserInputPort.execute(command);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", command);
            return "user/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        deleteUserInputPort.execute(new DeleteUserCommand(id));
        return "redirect:/users";
    }
}
