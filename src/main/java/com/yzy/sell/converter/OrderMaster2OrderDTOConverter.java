package com.yzy.sell.converter;

import com.yzy.sell.dataobject.OrderMaster;
import com.yzy.sell.dto.OrderDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: yzy
 * @create: 2018-03-25 22:12
 */
public class OrderMaster2OrderDTOConverter {


    public static OrderDTO convert(OrderMaster orderMaster){
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        return  orderDTO;
    }
    public static List<OrderDTO> convert(List<OrderMaster> orderMasterList){
        return orderMasterList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
