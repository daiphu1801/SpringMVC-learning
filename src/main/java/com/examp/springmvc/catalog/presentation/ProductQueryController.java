package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.query.CategoryDTO;
import com.examp.springmvc.catalog.application.category.query.FindAllCategoriesUseCase;
import com.examp.springmvc.catalog.application.product.query.FindAllProductsUseCase;
import com.examp.springmvc.catalog.application.product.query.FindProductByIdUseCase;
import com.examp.springmvc.catalog.application.product.query.ProductDTO;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class ProductQueryController {
    private final FindAllProductsUseCase findAllProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;

    public ProductQueryController(
            FindAllProductsUseCase findAllProductsUseCase,
            FindProductByIdUseCase findProductByIdUseCase,
            FindAllCategoriesUseCase findAllCategoriesUseCase) {
        this.findAllProductsUseCase = findAllProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.findAllCategoriesUseCase = findAllCategoriesUseCase;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<ProductDTO> products = findAllProductsUseCase.execute();
        model.addAttribute("products", products);
        return "catalog/product-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<CategoryDTO> categories = findAllCategoriesUseCase.execute();
        model.addAttribute("categories", categories);
        return "catalog/product-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ProductDTO product = findProductByIdUseCase.execute(id);
        List<CategoryDTO> categories = findAllCategoriesUseCase.execute();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "catalog/product-form";
    }
}
