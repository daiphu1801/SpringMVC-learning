package com.examp.springmvc.catalog.application.product.query;

import com.examp.springmvc.catalog.infrastructure.mapper.ProductMapper;
import com.examp.springmvc.catalog.infrastructure.persistence.ProductDbEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindProductByIdUseCase {
    private final ProductMapper productMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindProductByIdUseCase(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductDTO execute(Long id) {
        ProductDbEntity entity = productMapper.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id);
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
