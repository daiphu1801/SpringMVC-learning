package com.examp.springmvc.catalog.presentation;

import com.examp.springmvc.catalog.application.category.query.FindAllCategoriesUseCase;
import com.examp.springmvc.catalog.application.product.command.CreateProductCommand;
import com.examp.springmvc.catalog.application.product.command.CreateProductUseCase;
import com.examp.springmvc.catalog.application.product.command.DeleteProductUseCase;
import com.examp.springmvc.catalog.application.product.command.UpdateProductCommand;
import com.examp.springmvc.catalog.application.product.command.UpdateProductUseCase;
import com.examp.springmvc.catalog.application.product.query.FindAllProductsUseCase;
import com.examp.springmvc.catalog.application.product.query.FindProductByIdUseCase;
import com.examp.springmvc.catalog.domain.model.ProductStatus;
import com.examp.springmvc.shared.presentation.ImageFileValidator;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/products")
public class ProductCommandController {
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final FindAllProductsUseCase findAllProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;

    public ProductCommandController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            FindAllProductsUseCase findAllProductsUseCase,
            FindProductByIdUseCase findProductByIdUseCase,
            FindAllCategoriesUseCase findAllCategoriesUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.findAllProductsUseCase = findAllProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.findAllCategoriesUseCase = findAllCategoriesUseCase;
    }

    @PostMapping("/create")
    public String createProduct(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("sku") String sku,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") ProductStatus status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        try {
            InputStream imageStream = null;
            String imageName = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                ImageFileValidator.validate(imageFile);
                imageStream = imageFile.getInputStream();
                String ext = extractExtension(imageFile.getOriginalFilename());
                imageName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
            }
            CreateProductCommand command =
                    new CreateProductCommand(categoryId, sku, name, description, price, status, imageStream, imageName);
            createProductUseCase.execute(command);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", findAllCategoriesUseCase.execute());
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("sku", sku);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("price", price);
            model.addAttribute("status", status);
            return "catalog/product-form";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("sku") String sku,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") ProductStatus status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        try {
            InputStream imageStream = null;
            String imageName = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                ImageFileValidator.validate(imageFile);
                imageStream = imageFile.getInputStream();
                String ext = extractExtension(imageFile.getOriginalFilename());
                imageName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
            }
            UpdateProductCommand command = new UpdateProductCommand(
                    id, categoryId, sku, name, description, price, status, imageStream, imageName);
            updateProductUseCase.execute(command);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", findProductByIdUseCase.execute(id));
            model.addAttribute("categories", findAllCategoriesUseCase.execute());
            return "catalog/product-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Model model) {
        try {
            deleteProductUseCase.execute(id);
            return "redirect:/admin/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("products", findAllProductsUseCase.execute());
            return "catalog/product-list";
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
