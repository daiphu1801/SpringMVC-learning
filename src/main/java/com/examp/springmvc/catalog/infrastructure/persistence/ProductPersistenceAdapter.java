package com.examp.springmvc.catalog.infrastructure.persistence;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import com.examp.springmvc.catalog.infrastructure.mapper.ProductMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPersistenceAdapter implements ProductPersistencePort {
    private final ProductMapper productMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductPersistenceAdapter(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public void save(Product product) {
        ProductDbEntity dbEntity = toDbEntity(product);
        if (dbEntity.getId() == null) {
            productMapper.insert(dbEntity);
        } else {
            productMapper.update(dbEntity);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        ProductDbEntity entity = productMapper.findById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        ProductDbEntity entity = productMapper.findBySku(sku);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productMapper.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return productMapper.findByIds(ids).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        productMapper.deleteById(id);
    }

    private ProductDbEntity toDbEntity(Product domain) {
        ProductDbEntity entity = new ProductDbEntity();
        entity.setId(domain.getId());
        entity.setCategoryId(domain.getCategoryId());
        entity.setSku(domain.getSku());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStatus(domain.getStatus());
        entity.setImageUrl(domain.getImageUrl());
        entity.setStock(domain.getStock());
        return entity;
    }

    private Product toDomain(ProductDbEntity entity) {
        return new Product(
                entity.getId(),
                entity.getCategoryId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getImageUrl(),
                entity.getStock());
    }
}
