package com.yzy.sell.service;

import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;
import com.yzy.sell.dto.OrderDTO;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-04 20:20
 */
public interface PayService {
    PayResponse create(OrderDTO orderDTO);

    PayResponse notify(String notifyData);

    RefundResponse refund(OrderDTO orderDTO);
}
