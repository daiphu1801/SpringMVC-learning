package com.examp.springmvc.catalog.application.product.query;

import java.util.List;
import java.util.Optional;

public interface ProductQueryPort {
    List<ProductDTO> findAll();

    Optional<ProductDTO> findById(Long id);

    List<ProductDTO> findByCategoryId(Long categoryId);
}
