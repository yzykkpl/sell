package com.yzy.sell.service.impl;


import com.yzy.sell.converter.OrderMaster2OrderDTOConverter;
import com.yzy.sell.dataobject.OrderMaster;
import com.yzy.sell.dto.CartDTO;
import com.yzy.sell.enums.OrderStatusEnum;
import com.yzy.sell.enums.PayStatusEnum;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.repository.OrderMasterRepository;

import com.yzy.sell.service.PayService;
import com.yzy.sell.service.WebSocket;
import com.yzy.sell.utils.KeyUtil;
import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dataobject.OrderDetail;
import com.yzy.sell.dataobject.ProductInfo;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.repository.OrderDetailRepository;
import com.yzy.sell.service.OrderService;
import com.yzy.sell.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private PayService payService;

    @Autowired
    private WebSocket webSocket;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        Boolean flag=true;
        String snapName="";
        String snapIcon="";
        //1. 查询商品（数量, 价格）
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {
            ProductInfo productInfo =  productService.findById(orderDetail.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            //2. 计算订单总价
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                    .add(orderAmount);

            //订单详情入库
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderDetail);
            orderDetailRepository.save(orderDetail);
            if (flag){
                snapName=orderDetail.getProductName();
                snapIcon=orderDetail.getProductIcon();
                flag=false;
            }

//            CartDTO cartDTO = new CartDTO(orderDetail.getProductId(), orderDetail.getProductQuantity());
//            cartDTOList.add(cartDTO);
        }
        flag=true;



        //3. 写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();

        orderDTO.setOrderId(orderId);
        orderDTO.setSnapIcon(snapIcon);
        orderDTO.setSnapName(snapName);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        //4. 扣库存

        List<CartDTO> cartDTOList=orderDTO.getOrderDetailList().stream()
                .map(e->new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);


        return orderDTO;
    }

    @Override
    public OrderDTO findById(String orderId) {
        OrderMaster orderMaster = orderMasterRepository.findById(orderId).get();
        if(orderMaster==null) throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if(orderDetailList.isEmpty()) throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);

        OrderDTO orderDTO=new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageable);
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        Page<OrderDTO> orderDTOPage =  new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {

        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("取消订单：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态为取消
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("取消订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //返还库存
        if(orderDTO.getOrderDetailList().isEmpty()){
            log.error("取消订单,详情为空：orderDTO={},",orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList=orderDTO.getOrderDetailList().stream()
                .map(e->new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        //如果已支付，需要退款
        if(orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            //TODO 将取消和退款分开，买家端只能申请退款和取消，卖家端可以退款和取消
            payService.refund(orderDTO);
            orderMaster.setPayStatus(PayStatusEnum.REFUNDED.getCode());
            orderMasterRepository.save(orderMaster);
        }
        return orderDTO;
    }

    @Override
    public OrderDTO userCancel(OrderDTO orderDTO) {
        //判断订单状态
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())){
            log.error("买家取消订单，支付状态错误：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改订单状态为取消
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("取消订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //返还库存
        if(orderDTO.getOrderDetailList().isEmpty()){
            log.error("取消订单,详情为空：orderDTO={},",orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList=orderDTO.getOrderDetailList().stream()
                .map(e->new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) {
        //1 判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("完结订单，状态不正确：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //2 修改订单状态
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("完结订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(OrderDTO orderDTO) {
        //1 判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("支付订单，订单状态不正确：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //2 判断支付状态
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())){
            log.error("支付订单，支付状态不正确：orderId={},payStatus={}",orderDTO.getOrderId(),orderDTO.getPayStatus());
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //3 修改支付状态
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("支付订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //webSocket
        webSocket.sendMessage("有新的订单!");
        return orderDTO;


    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        Page<OrderDTO> orderDTOPage =  new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }
    @Override
    public Page<OrderDTO> findListByOrderStatus(Integer orderStatus,Pageable pageable){
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatus(orderStatus,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        Page<OrderDTO> orderDTOPage =  new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }

    @Override
    @Transactional
    public OrderDTO applyForRefund(OrderDTO orderDTO) {

        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("申请退款订单：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            log.error("申请退款,但是支付状态不正确：orderId={},orderStatus={}，payStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus(),orderDTO.getPayStatusEnum().getMessage());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态为申请退款
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.APPLY_FOR_REFUND.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("申请退款订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    /**
     * 退款(只能卖家端操作)
     * @param: 
     */
    @Override
    @Transactional
    public OrderDTO refund(OrderDTO orderDTO) {

        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.APPLY_FOR_REFUND)){
            log.error("卖家端退款，订单状态不正确：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            log.error("卖家端退款,支付状态不正确：orderId={},orderStatus={}，payStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus(),orderDTO.getPayStatusEnum().getMessage());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态和支付状态
        OrderMaster orderMaster=new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        orderDTO.setPayStatus(PayStatusEnum.REFUNDED.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("申请退款订单：orderMaster={},",updateResult);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }
}
