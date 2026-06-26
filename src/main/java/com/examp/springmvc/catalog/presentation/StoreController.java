package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.query.CategoryDTO;
import com.examp.springmvc.catalog.application.category.query.FindAllCategoriesUseCase;
import com.examp.springmvc.catalog.application.product.query.FindAllProductsUseCase;
import com.examp.springmvc.catalog.application.product.query.FindProductByIdUseCase;
import com.examp.springmvc.catalog.application.product.query.ProductDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
public class StoreController {
    private final FindAllProductsUseCase findAllProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;

    public StoreController(
            FindAllProductsUseCase findAllProductsUseCase,
            FindProductByIdUseCase findProductByIdUseCase,
            FindAllCategoriesUseCase findAllCategoriesUseCase) {
        this.findAllProductsUseCase = findAllProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.findAllCategoriesUseCase = findAllCategoriesUseCase;
    }

    @GetMapping
    public String displayStore(@RequestParam(value = "category", required = false) Long categoryId, Model model) {
        List<CategoryDTO> categories = findAllCategoriesUseCase.execute();
        List<ProductDTO> allProducts;
        if (categoryId != null) {
            allProducts = findAllProductsUseCase.executeByCategoryId(categoryId);
        } else {
            allProducts = findAllProductsUseCase.execute();
        }

        // Only show ACTIVE products in storefront for customers
        List<ProductDTO> activeProducts = allProducts.stream()
                .filter(p -> p.getStatus() == com.examp.springmvc.catalog.domain.model.ProductStatus.ACTIVE)
                .collect(Collectors.toList());

        model.addAttribute("categories", categories);
        model.addAttribute("products", activeProducts);
        model.addAttribute("selectedCategoryId", categoryId);
        return "catalog/store-front";
    }

    @GetMapping("/{id}")
    public String displayProductDetail(@PathVariable("id") Long id, Model model) {
        ProductDTO product = findProductByIdUseCase.execute(id);
        model.addAttribute("product", product);
        return "catalog/product-detail";
    }
}
