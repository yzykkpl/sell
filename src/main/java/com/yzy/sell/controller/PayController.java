package com.yzy.sell.controller;

import com.lly835.bestpay.model.PayResponse;
import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.service.OrderService;
import com.yzy.sell.service.PayService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.viewobject.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 微信支付
 * @author: yzy
 * @create: 2018-04-04 16:23
 */
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 生成预付订单
     *
     * @param:
     */
    @GetMapping("/create")
    public ResultVO<PayResponse> create(@RequestParam String orderId){
        //1.查询订单
        OrderDTO orderDTO = orderService.findById(orderId);
        if(orderDTO==null){
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        //2.发起支付
        PayResponse payResponse = payService.create(orderDTO);

        return ResultVOUtil.success(payResponse);
    }

    @RequestMapping("/notify")
    public ModelAndView notify(@RequestBody String notifyData){

        //log.info("回调XML={}",notifyData);

        payService.notify(notifyData);

        return new ModelAndView("pay/success");

    }
    @GetMapping("/isOnPay")
    public ResultVO<Map<String,Boolean>> isOnPay(){
        Boolean isOnPay=true;
        String status=redisTemplate.opsForValue().get("pay");
        if(status.equals("false")) isOnPay=false;
        Map<String,Boolean> map=new HashMap<>();
        map.put("isOnPay",isOnPay);
        return ResultVOUtil.success(map);
    }

}
