package com.yzy.sell.controller;

import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.enums.OrderStatusEnum;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.service.OrderService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.utils.SortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.NoSuchElementException;


/**
 * @description: 卖家端订单
 * @author: yzy
 * @create: 2018-03-30 19:56
 */
@Controller
@Slf4j
@RequestMapping("/seller/order")
public class SellerOrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "filter",defaultValue = "-1") Integer filter,
                            @RequestParam(value="page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size, Map<String,Object> map){
        Pageable pageable= PageRequest.of(page-1,size, SortUtil.basicSort("desc", "createTime"));
        Page<OrderDTO> orderDTOPage=null;
        if(filter==-1)
            orderDTOPage=orderService.findList(pageable);
        else
            orderDTOPage=orderService.findListByOrderStatus(filter,pageable);
        Integer difference=size-orderDTOPage.getContent().size();
        String status=redisTemplate.opsForValue().get("pay");
        map.put("orderDTOPage",orderDTOPage);
        map.put("currentPage",page);
        map.put("size",size);
        map.put("difference",difference);
        map.put("filter",filter);
        map.put("status",status);
        ModelAndView model =new ModelAndView("order/list",map);

        return model;

    }
    /**
     * 取消订单
     * @param:
     */
    @GetMapping("/cancel")
    public ModelAndView cancel(@RequestParam("orderId") String orderId,Map<String,Object> map){
        try {
            OrderDTO orderDTO = orderService.findById(orderId);
            orderService.cancel(orderDTO);
        }catch (NoSuchElementException e){
            log.error("卖家端取消订单异常：查询不到订单");
            map.put("msg", ResultEnum.ORDER_NOT_EXIST.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }catch (SellException e){
            log.error("卖家端取消订单异常={}",e.getMessage());
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }
        map.put("msg",ResultEnum.ORDER_CANCEL_SUCCESS.getMessage());
        map.put("url","/sell/seller/order/list");
        return new ModelAndView("common/success");
    }
    /**
     * 退款
     * @param:
     */
    @GetMapping("/refund")
    public ModelAndView refund(@RequestParam("orderId") String orderId,Map<String,Object> map){
        try {
            OrderDTO orderDTO = orderService.findById(orderId);
            orderService.refund(orderDTO);
        }catch (NoSuchElementException e){
            log.error("卖家端退款订单异常：查询不到订单");
            map.put("msg", ResultEnum.ORDER_NOT_EXIST.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }catch (SellException e){
            log.error("卖家端退款订单异常={}",e.getMessage());
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }
        map.put("msg",ResultEnum.ORDER_CANCEL_SUCCESS.getMessage());
        map.put("url","/sell/seller/order/list");
        return new ModelAndView("common/success");
    }
    /**
     * 订单详情
     * @param:
     */
    @GetMapping("/detail")
    public ModelAndView detail(@RequestParam("orderId") String orderId,Map<String,Object> map){
        OrderDTO orderDTO;
        try {
            orderDTO = orderService.findById(orderId);

        }catch (NoSuchElementException e){
            log.error("卖家端异常：查询不到订单");
            map.put("msg", ResultEnum.ORDER_NOT_EXIST.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }catch (SellException e){
            log.error("卖家端异常={}",e.getMessage());
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }
        map.put("orderDTO",orderDTO);
        return new ModelAndView("order/detail",map);
    }
    /**
     * 完结订单
     * @param:
     */
    @GetMapping("/finish")
    public ModelAndView finish(@RequestParam("orderId") String orderId,Map<String,Object> map){
        try {
            OrderDTO orderDTO = orderService.findById(orderId);
            orderService.finish(orderDTO);
        }catch (NoSuchElementException e){
            log.error("卖家端完结订单异常：查询不到订单");
            map.put("msg", ResultEnum.ORDER_NOT_EXIST.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }catch (SellException e){
            log.error("卖家端完结订单异常={}",e.getMessage());
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("common/error",map);
        }
        map.put("msg",ResultEnum.ORDER_FINISH_SUCCESS.getMessage());
        map.put("url","/sell/seller/order/list");
        return new ModelAndView("common/success");
    }
    @GetMapping("/setPay")
    public ModelAndView setPay(@RequestParam String status,Map<String,Object> map){

        redisTemplate.opsForValue().set("pay",status);
        map.put("url","/sell/seller/order/list");
        return new ModelAndView("common/success",map);
    }

}
