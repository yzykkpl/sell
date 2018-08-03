package com.yzy.sell.controller;

import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dataobject.ProductCategory;
import com.yzy.sell.dataobject.ProductInfo;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.form.ProductForm;
import com.yzy.sell.service.CategoryService;
import com.yzy.sell.service.ProductService;
import com.yzy.sell.utils.KeyUtil;
import com.yzy.sell.utils.SortUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @description: 卖家商品
 * @author: yzy
 * @create: 2018-03-31 21:44
 */
@Controller
@RequestMapping("/seller/product")
public class SellerProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value="page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size, Map<String,Object> map){
        Pageable pageable= PageRequest.of(page-1,size,SortUtil.basicSort("desc", "createTime"));
        Page<ProductInfo> productInfoPage=productService.findAll(pageable);
        Integer difference=size-productInfoPage.getContent().size();
        map.put("productInfoPage",productInfoPage);
        map.put("currentPage",page);
        map.put("size",size);
        map.put("difference",difference);
        return new ModelAndView("product/list",map);
    }

    @GetMapping("/on_sale")
    public ModelAndView onSale(@RequestParam("productId") String productId,Map<String,Object> map){
        try{
            productService.onSale(productId);
        }catch (SellException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }catch (NoSuchElementException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }
        map.put("url","/sell/seller/product/list");
        return new ModelAndView("common/success",map);
    }
    @GetMapping("/off_sale")
    public ModelAndView offSale(@RequestParam("productId") String productId,Map<String,Object> map){
        try{
            productService.offSale(productId);
        }catch (SellException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }catch (NoSuchElementException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }
        map.put("url","/sell/seller/product/list");
        return new ModelAndView("common/success",map);
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam(value = "productId",required = false) String productId,Map<String,Object> map){
        if(productId!=null&&!productId.isEmpty()){
            ProductInfo productInfo=productService.findById(productId);
            map.put("productInfo",productInfo);
        }

        //查询所有类目
        List<ProductCategory> categoryList=categoryService.findAll();
        map.put("categoryList",categoryList);
        return new ModelAndView("product/index",map);
    }
    
    /**
     * 商品新增/更新
     * @param: 
     */
    @PostMapping("/save")
    public ModelAndView save(@Valid ProductForm productForm, BindingResult bindingResult,Map<String,Object> map){
        if(bindingResult.hasErrors()){
            map.put("msg",bindingResult.getFieldError().getDefaultMessage());
            map.put("url","/sell/seller/product/index?"+productForm.getProductId());
            return new ModelAndView("common/error",map);
        }
        ProductInfo productInfo=new ProductInfo();
        try {
            if(productForm.getProductId().isEmpty()){
                productForm.setProductId(KeyUtil.genUniqueKey());
            }
            else {
                productInfo = productService.findById(productForm.getProductId());
            }
            BeanUtils.copyProperties(productForm,productInfo);
            productService.save(productInfo);
        }catch (Exception e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/index?"+productForm.getProductId());
            return new ModelAndView("common/error",map);
        }
        map.put("url","/sell/seller/product/list");
        return new ModelAndView("common/success",map);
    }

}
