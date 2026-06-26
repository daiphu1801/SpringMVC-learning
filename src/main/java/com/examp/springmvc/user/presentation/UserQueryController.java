package com.examp.springmvc.user.presentation;

import com.examp.springmvc.user.application.usermanagement.query.FindAllUsersInputPort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserQueryController {

    private final FindAllUsersInputPort findAllUsersInputPort;
    private final FindUserByIdInputPort findUserByIdInputPort;

    public UserQueryController(
            FindAllUsersInputPort findAllUsersInputPort, FindUserByIdInputPort findUserByIdInputPort) {
        this.findAllUsersInputPort = findAllUsersInputPort;
        this.findUserByIdInputPort = findUserByIdInputPort;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", findAllUsersInputPort.execute());
        return "user/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        com.examp.springmvc.user.application.usermanagement.command.CreateUserCommand command =
                new com.examp.springmvc.user.application.usermanagement.command.CreateUserCommand();
        command.setStatus("ACTIVE");
        model.addAttribute("user", command);
        return "user/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", findUserByIdInputPort.execute(id));
        return "user/form";
    }
}
