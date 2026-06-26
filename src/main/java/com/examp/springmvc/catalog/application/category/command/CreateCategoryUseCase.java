package com.examp.springmvc.catalog.application.category.command;

import com.examp.springmvc.catalog.domain.model.Category;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoryUseCase {
    private final CategoryPersistencePort categoryPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CreateCategoryUseCase(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Transactional
    public void execute(CreateCategoryCommand command) {
        String cleanCode = command.getCode() != null ? command.getCode().trim().toLowerCase() : "";
        if (categoryPersistencePort.findByCode(cleanCode).isPresent()) {
            throw new IllegalArgumentException("Mã danh mục '" + cleanCode + "' đã tồn tại!");
        }

        Category category = new Category(null, command.getName(), command.getCode(), command.getDescription());

        categoryPersistencePort.save(category);
    }
}
