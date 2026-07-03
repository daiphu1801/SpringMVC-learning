package com.examp.springmvc.user.presentation;

import com.examp.springmvc.shared.domain.dto.PagedResult;
import com.examp.springmvc.user.application.usermanagement.command.UpdateUserCommand;
import com.examp.springmvc.user.application.usermanagement.query.FindAllUsersInputPort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        PagedResult<UserDTO> pagedResult = findAllUsersInputPort.execute(page, size);
        model.addAttribute("pagedResult", pagedResult);
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
        UserDTO dto = findUserByIdInputPort.execute(id);
        UpdateUserCommand command = new UpdateUserCommand(
                dto.getId(),
                dto.getUsername(),
                dto.getFullName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getStatus(),
                null,
                dto.getRole());
        model.addAttribute("user", command);
        return "user/form";
    }
}
