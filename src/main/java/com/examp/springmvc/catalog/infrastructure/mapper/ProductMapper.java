package com.examp.springmvc.catalog.infrastructure.mapper;

import com.examp.springmvc.catalog.infrastructure.persistence.ProductDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    void insert(ProductDbEntity entity);

    void update(ProductDbEntity entity);

    ProductDbEntity findById(Long id);

    ProductDbEntity findBySku(String sku);

    List<ProductDbEntity> findAll();

    List<ProductDbEntity> findByCategoryId(Long categoryId);

    void deleteById(Long id);
}
