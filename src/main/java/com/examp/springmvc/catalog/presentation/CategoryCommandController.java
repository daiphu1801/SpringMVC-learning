package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.command.CreateCategoryCommand;
import com.examp.springmvc.catalog.application.category.command.CreateCategoryUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/categories")
public class CategoryCommandController {
    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryCommandController(CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
    }

    @PostMapping("/create")
    public String createCategory(
            @RequestParam("name") String name,
            @RequestParam("code") String code,
            @RequestParam("description") String description,
            Model model) {
        try {
            CreateCategoryCommand command = new CreateCategoryCommand(name, code, description);
            createCategoryUseCase.execute(command);
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("code", code);
            model.addAttribute("description", description);
            return "catalog/category-form";
        }
    }
}
