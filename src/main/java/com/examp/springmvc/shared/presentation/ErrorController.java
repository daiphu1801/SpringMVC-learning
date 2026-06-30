package com.examp.springmvc.shared.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/400")
    public String handleError400() {
        return "error/400";
    }

    @RequestMapping("/404")
    public String handleError404() {
        return "error/404";
    }

    @RequestMapping("/500")
    public String handleError500() {
        return "error/500";
    }
}
