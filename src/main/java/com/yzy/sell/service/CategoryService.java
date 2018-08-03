package com.yzy.sell.service;

import com.yzy.sell.dataobject.ProductCategory;

import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;

public interface CategoryService {
    ProductCategory findById(Integer categoryId);
   List<ProductCategory> findAll();
   List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);
   ProductCategory save(ProductCategory productCategory);
}
