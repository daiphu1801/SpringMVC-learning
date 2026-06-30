package com.examp.springmvc.catalog.application.product.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindProductByIdUseCase {
    private final ProductQueryPort productQueryPort;

    public FindProductByIdUseCase(ProductQueryPort productQueryPort) {
        this.productQueryPort = productQueryPort;
    }

    @Transactional(readOnly = true)
    public ProductDTO execute(Long id) {
        return productQueryPort
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id));
    }
}
