package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.command.CreateCategoryCommand;
import com.examp.springmvc.catalog.application.category.command.CreateCategoryUseCase;
import com.examp.springmvc.catalog.application.category.query.CategoryDTO;
import com.examp.springmvc.catalog.application.category.query.FindAllCategoriesUseCase;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;

    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase, FindAllCategoriesUseCase findAllCategoriesUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.findAllCategoriesUseCase = findAllCategoriesUseCase;
    }

    @GetMapping
    public String listCategories(Model model) {
        List<CategoryDTO> categories = findAllCategoriesUseCase.execute();
        model.addAttribute("categories", categories);
        return "catalog/category-list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "catalog/category-form";
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
