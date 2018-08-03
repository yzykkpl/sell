package com.yzy.sell.controller;

import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.converter.OrderForm2OrderDTOConverter;
import com.yzy.sell.dataobject.OrderDetail;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.form.OrderForm;
import com.yzy.sell.repository.OrderDetailRepository;
import com.yzy.sell.service.BuyerService;
import com.yzy.sell.service.OrderService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.utils.SortUtil;
import com.yzy.sell.viewobject.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 买家订单
 * @author: yzy
 * @create: 2018-03-26 18:52
 */
@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BuyerService buyerService;
    //创建订单
    @PostMapping("/create")
    public ResultVO<Map<String,String>> create(@Valid OrderForm orderForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("创建订单，参数不正确，orderForm={}",orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode()
                    ,bindingResult.getFieldError().getDefaultMessage());
        }
        String openid=buyerService.getOpenid(orderForm.getToken());
        if(openid.equals("e")){
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        orderForm.setOpenid(openid);
        OrderDTO orderDTO= OrderForm2OrderDTOConverter.convert(orderForm);
        if(orderDTO.getOrderDetailList().isEmpty()){
            log.error("购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO createResult=orderService.create(orderDTO);

        Map<String,String> map=new HashMap<>();
        map.put("orderId",createResult.getOrderId());

        return ResultVOUtil.success(map);
    }
    //订单列表
    @GetMapping("/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("token") String token,
                                         @RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "5") Integer size){
        String openid=buyerService.getOpenid(token);
        if(openid.equals("e")){
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        Pageable pageable=PageRequest.of(page,size,SortUtil.basicSort("desc", "createTime"));
        Page<OrderDTO> orderDTOPage=orderService.findList(openid,pageable);
        return ResultVOUtil.success(orderDTOPage.getContent());
    }
    //订单详情
    @GetMapping("/detail")
    public ResultVO<OrderDTO> detail (@RequestParam("token") String token,
                                      @RequestParam("orderId") String orderId){
        String openid=buyerService.getOpenid(token);
        if(openid.equals("e")){
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        OrderDTO orderDTO=buyerService.findOrderOne(openid,orderId);
        return ResultVOUtil.success(orderDTO);
    }
    //申请退款
    //用户暂不可自动退款
    @PostMapping("/refund")
    public ResultVO refund (@RequestParam("token") String token,
                                      @RequestParam("orderId") String orderid){
        String openid=buyerService.getOpenid(token);
        if(openid.equals("e")){
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        buyerService.refundOrderOne(openid,orderid);
        return ResultVOUtil.success();
    }

    //取消未支付订单
    //用户暂不可自动退款
    @PostMapping("/cancel")
    public ResultVO userCancel (@RequestParam("token") String token,
                            @RequestParam("orderId") String orderid){
        String openid=buyerService.getOpenid(token);
        if(openid.equals("e")){
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        buyerService.cancelOrderOne(openid,orderid);
        return ResultVOUtil.success();
    }
}
