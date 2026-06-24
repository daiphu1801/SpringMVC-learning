package com.examp.springmvc.controller;

import com.examp.springmvc.model.User;
import com.examp.springmvc.service.UserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());

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

        userService.create(user);

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("user", userService.findById(id));

        return "user/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("user") User user) {

        user.setId(id);
        userService.update(user);

        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {

        userService.deleteById(id);

        return "redirect:/users";
    }
}
