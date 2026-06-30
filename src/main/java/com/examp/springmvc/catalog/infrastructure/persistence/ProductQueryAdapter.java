package com.examp.springmvc.catalog.infrastructure.persistence;

import com.examp.springmvc.catalog.application.product.query.ProductDTO;
import com.examp.springmvc.catalog.application.product.query.ProductQueryPort;
import com.examp.springmvc.catalog.infrastructure.mapper.ProductMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductMapper productMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductQueryAdapter(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> findAll() {
        List<ProductDbEntity> entities = productMapper.findAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDTO> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        ProductDbEntity entity = productMapper.findById(id);
        return Optional.ofNullable(toDTO(entity));
    }

    @Override
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        List<ProductDbEntity> entities = productMapper.findByCategoryId(categoryId);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ProductDTO toDTO(ProductDbEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProductDTO(
                entity.getId(),
                entity.getCategoryId(),
                entity.getCategoryName(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getImageUrl());
    }
}
