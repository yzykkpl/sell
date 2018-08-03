package com.yzy.sell.service.impl;

import com.yzy.sell.dataobject.ProductCategory;
import com.yzy.sell.repository.ProductCategoryRepository;
import com.yzy.sell.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @description: CategoryServiceImpl
 * @author: yzy
 * @create: 2018-03-24 22:29
 */

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private ProductCategoryRepository repository;

    @Override
    public ProductCategory findById(Integer categoryId) {
        return repository.findById(categoryId).get();
    }

    @Override
    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {
        return repository.findByCategoryTypeIn(categoryTypeList);
    }

    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return repository.save(productCategory);
    }
}
