package com.yzy.sell.service;

import com.yzy.sell.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public interface OrderService {

    /** 创建订单. */
    OrderDTO create(OrderDTO orderDTO);

    /** 查询单个订单. */
    OrderDTO findById(String orderId);

    /** 查询订单列表. */
    Page<OrderDTO> findList(String buyerOpenid, Pageable pageable);

    /** 卖家取消订单. */
    OrderDTO cancel(OrderDTO orderDTO);

    /** 买家取消订单. */
    OrderDTO userCancel(OrderDTO orderDTO);
    /** 完结订单. */
    OrderDTO finish(OrderDTO orderDTO);

    /** 支付订单. */
    OrderDTO paid(OrderDTO orderDTO);

    /** 查询订单列表. */
    Page<OrderDTO> findList(Pageable pageable);

    /** 买家申请退款 */
    OrderDTO applyForRefund(OrderDTO orderDTO);
    /** 退款 */
    OrderDTO refund(OrderDTO orderDTO);

    /** 查找订单列表-根据订单状态 */
    Page<OrderDTO> findListByOrderStatus(Integer orderStatus,Pageable pageable);



}
