package com.examp.springmvc.catalog.infrastructure.persistence;

import com.examp.springmvc.catalog.domain.model.Category;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import com.examp.springmvc.catalog.infrastructure.mapper.CategoryMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryPersistenceAdapter implements CategoryPersistencePort {
    private final CategoryMapper categoryMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CategoryPersistenceAdapter(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void save(Category category) {
        CategoryDbEntity dbEntity = toDbEntity(category);
        if (dbEntity.getId() == null) {
            categoryMapper.insert(dbEntity);
        } else {
            categoryMapper.update(dbEntity);
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        CategoryDbEntity entity = categoryMapper.findById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Category> findByCode(String code) {
        CategoryDbEntity entity = categoryMapper.findByCode(code);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }

    private CategoryDbEntity toDbEntity(Category domain) {
        CategoryDbEntity entity = new CategoryDbEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    private Category toDomain(CategoryDbEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getCode(), entity.getDescription());
    }
}
