package com.examp.springmvc.user.infrastructure.web;

import com.examp.springmvc.user.application.usecase.CreateUserUseCase;
import com.examp.springmvc.user.application.usecase.DeleteUserUseCase;
import com.examp.springmvc.user.application.usecase.FindAllUsersUseCase;
import com.examp.springmvc.user.application.usecase.FindUserByIdUseCase;
import com.examp.springmvc.user.application.usecase.UpdateUserUseCase;
import com.examp.springmvc.user.domain.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final FindAllUsersUseCase findAllUsersUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(
            FindAllUsersUseCase findAllUsersUseCase,
            FindUserByIdUseCase findUserByIdUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase) {
        this.findAllUsersUseCase = findAllUsersUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", findAllUsersUseCase.execute());
        return "user/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User user = new User();
        user.setStatus("ACTIVE");
        model.addAttribute("user", user);
        return "user/form";
    }

    @PostMapping
    public String create(@ModelAttribute("user") User user) {
        createUserUseCase.execute(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", findUserByIdUseCase.execute(id));
        return "user/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("user") User user) {
        user.setId(id);
        updateUserUseCase.execute(user);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        deleteUserUseCase.execute(id);
        return "redirect:/users";
    }
}
