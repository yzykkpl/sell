package com.yzy.sell.controller;

import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dataobject.ProductCategory;
import com.yzy.sell.dataobject.ProductInfo;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.service.CategoryService;
import com.yzy.sell.service.ProductService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.utils.SortUtil;
import com.yzy.sell.viewobject.ProductInfoVO;
import com.yzy.sell.viewobject.ProductVO;
import com.yzy.sell.viewobject.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 买家商品controller
 * @author: yzy
 * @create: 2018-03-25 11:03
 */

@RestController
@RequestMapping("/buyer/product")
@Slf4j
public class BuyerProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    //查询所有商品（按类目返回）
    @GetMapping("/list")
    public ResultVO list() {
        //1 查询所有上架商品(降序)

        List<ProductInfo> productInfoList = productService.findUpAll(SortUtil.basicSort("desc", "createTime"));
        List<ProductInfoVO> productInfoVOListAll=new ArrayList<>();
        List<ProductVO> productVOList = new ArrayList<>();
        //先将所有商品放入一个list
        productInfoList.forEach(productInfo -> {
            ProductInfoVO productInfoVO = new ProductInfoVO();
            BeanUtils.copyProperties(productInfo, productInfoVO);
            productInfoVOListAll.add(productInfoVO);
        });
        //所有商品单独放在一个productVO里
        ProductVO allProductVO=new ProductVO();
        allProductVO.setCategoryType(0);
        allProductVO.setCategoryName("所有商品");
        allProductVO.setProductInfoVOList(productInfoVOListAll);
        productVOList.add(allProductVO);
        //2 查询类目（一次性查询）
        List<Integer> categoryTypeList = productInfoList.stream()
                .map(e -> e.getCategoryType())
                .collect(Collectors.toList());
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);

        //3 数据拼装

        for (ProductCategory productCategory : productCategoryList) {
            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(productCategory.getCategoryName());
            productVO.setCategoryType(productCategory.getCategoryType());
            productVO.setCategoryIcon(productCategory.getCategoryIcon());
            List<ProductInfoVO> productInfoVOList = new ArrayList<>();

            for (ProductInfo productInfo : productInfoList) {
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {
                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    BeanUtils.copyProperties(productInfo, productInfoVO);
                    productInfoVOList.add(productInfoVO);
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }


        return ResultVOUtil.success(productVOList);
    }


}

