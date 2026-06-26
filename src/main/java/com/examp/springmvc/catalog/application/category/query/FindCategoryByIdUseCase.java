package com.examp.springmvc.catalog.application.category.query;

import com.examp.springmvc.catalog.domain.model.Category;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;

@Service
public class FindCategoryByIdUseCase {
    private final CategoryPersistencePort categoryPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindCategoryByIdUseCase(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    public CategoryDTO execute(Long id) {
        Category c = categoryPersistencePort
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id));
        return new CategoryDTO(c.getId(), c.getName(), c.getCode(), c.getDescription());
    }
}
