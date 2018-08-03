package com.yzy.sell.repository;

import com.yzy.sell.dataobject.ProductInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInfoRepository extends JpaRepository<ProductInfo,String>{

    List<ProductInfo> findByProductStatus(Integer productStatus,Sort sort);
    List<ProductInfo> findByCategoryType(Integer categoryType);
}
