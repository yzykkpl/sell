package com.yzy.sell.repository;

import com.yzy.sell.dataobject.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
     //该方法查询结果没有重复值
     List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);
}