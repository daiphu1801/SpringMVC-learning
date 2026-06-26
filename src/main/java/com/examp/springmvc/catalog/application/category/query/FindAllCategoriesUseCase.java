package com.examp.springmvc.catalog.application.category.query;

import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FindAllCategoriesUseCase {
    private final CategoryPersistencePort categoryPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindAllCategoriesUseCase(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    public List<CategoryDTO> execute() {
        return categoryPersistencePort.findAll().stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getCode(), c.getDescription()))
                .collect(Collectors.toList());
    }
}
