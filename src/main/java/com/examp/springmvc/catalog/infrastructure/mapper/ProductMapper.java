package com.examp.springmvc.catalog.infrastructure.mapper;

import com.examp.springmvc.catalog.infrastructure.persistence.ProductDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {
    void insert(ProductDbEntity entity);

    void update(ProductDbEntity entity);

    ProductDbEntity findById(Long id);

    List<ProductDbEntity> findByIds(@Param("ids") List<Long> ids);

    ProductDbEntity findBySku(String sku);

    List<ProductDbEntity> findAll();

    List<ProductDbEntity> findByCategoryId(Long categoryId);

    void deleteById(Long id);
}
