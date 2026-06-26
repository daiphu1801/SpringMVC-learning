package com.examp.springmvc.catalog.domain.ports.output;

import com.examp.springmvc.catalog.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {
    void save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findBySku(String sku);

    List<Product> findAll();

    List<Product> findByCategoryId(Long categoryId);

    void deleteById(Long id);
}
