package com.examp.springmvc.catalog.domain.ports.output;

import com.examp.springmvc.catalog.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryPersistencePort {
    void save(Category category);

    Optional<Category> findById(Long id);

    Optional<Category> findByCode(String code);

    List<Category> findAll();

    void deleteById(Long id);
}
