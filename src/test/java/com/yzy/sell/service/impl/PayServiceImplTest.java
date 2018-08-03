package com.yzy.sell.service.impl;

import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.service.OrderService;
import com.yzy.sell.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PayServiceImplTest {
    @Autowired
    private PayService payService;
    @Autowired
    private OrderService orderService;

    @Test
    public void create() throws Exception{
        OrderDTO orderDTO=orderService.findById("1522849211592318873");
        payService.create(orderDTO);
    }
    @Test
    public void refund(){
        OrderDTO orderDTO=orderService.findById("1522849211592318873");
        payService.refund(orderDTO);
    }

}