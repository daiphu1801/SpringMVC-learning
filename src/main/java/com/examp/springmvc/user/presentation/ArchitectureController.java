package com.examp.springmvc.user.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchitectureController {

    @GetMapping("/architecture")
    public String showArchitecture() {
        return "architecture";
    }
}
