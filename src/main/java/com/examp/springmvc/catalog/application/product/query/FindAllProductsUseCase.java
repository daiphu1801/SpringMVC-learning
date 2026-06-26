package com.examp.springmvc.catalog.application.product.query;

import com.examp.springmvc.catalog.infrastructure.mapper.ProductMapper;
import com.examp.springmvc.catalog.infrastructure.persistence.ProductDbEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAllProductsUseCase {
    private final ProductMapper productMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindAllProductsUseCase(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> execute() {
        List<ProductDbEntity> entities = productMapper.findAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> executeByCategoryId(Long categoryId) {
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
