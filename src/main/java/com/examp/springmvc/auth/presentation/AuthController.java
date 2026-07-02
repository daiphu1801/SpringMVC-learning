package com.examp.springmvc.auth.presentation;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
import com.examp.springmvc.auth.application.ports.input.LoginInputPort;
import com.examp.springmvc.auth.application.ports.input.LogoutInputPort;
import com.examp.springmvc.auth.application.ports.input.RegisterCommand;
import com.examp.springmvc.auth.application.ports.input.RegisterInputPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final LoginInputPort loginInputPort;
    private final LogoutInputPort logoutInputPort;
    private final RegisterInputPort registerInputPort;

    public AuthController(
            LoginInputPort loginInputPort, LogoutInputPort logoutInputPort, RegisterInputPort registerInputPort) {
        this.loginInputPort = loginInputPort;
        this.logoutInputPort = logoutInputPort;
        this.registerInputPort = registerInputPort;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/users";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model) {
        try {
            AuthenticatedUserDTO user = loginInputPort.execute(username, password);
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.setAttribute("currentUser", user);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        logoutInputPort.execute();
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/users";
        }
        model.addAttribute("user", new RegisterCommand());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterCommand command, Model model) {
        try {
            registerInputPort.execute(command);
            model.addAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", command);
            return "auth/register";
        }
    }
}
