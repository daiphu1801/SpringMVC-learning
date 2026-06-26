package com.examp.springmvc.catalog.infrastructure.mapper;

import com.examp.springmvc.catalog.infrastructure.persistence.CategoryDbEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    void insert(CategoryDbEntity entity);

    void update(CategoryDbEntity entity);

    CategoryDbEntity findById(Long id);

    CategoryDbEntity findByCode(String code);

    List<CategoryDbEntity> findAll();

    void deleteById(Long id);
}
