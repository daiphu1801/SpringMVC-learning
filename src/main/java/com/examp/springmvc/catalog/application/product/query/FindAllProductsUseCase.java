package com.examp.springmvc.catalog.application.product.query;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAllProductsUseCase {
    private final ProductQueryPort productQueryPort;

    public FindAllProductsUseCase(ProductQueryPort productQueryPort) {
        this.productQueryPort = productQueryPort;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> execute() {
        return productQueryPort.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> executeByCategoryId(Long categoryId) {
        return productQueryPort.findByCategoryId(categoryId);
    }
}
