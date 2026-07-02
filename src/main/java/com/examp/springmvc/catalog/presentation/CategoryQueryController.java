package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.query.CategoryDTO;
import com.examp.springmvc.catalog.application.category.query.FindAllCategoriesUseCase;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
public class CategoryQueryController {
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;

    public CategoryQueryController(FindAllCategoriesUseCase findAllCategoriesUseCase) {
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
}
