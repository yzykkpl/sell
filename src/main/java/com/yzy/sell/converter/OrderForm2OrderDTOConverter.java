package com.yzy.sell.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.dataobject.OrderDetail;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.form.OrderForm;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: yzy
 * @create: 2018-03-26 19:22
 */
@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        Gson gson = new Gson();

        Integer totalAccount=0;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(), new TypeToken<List<OrderDetail>>() {
            }.getType());

            for(OrderDetail item:orderDetailList){
                totalAccount +=item.getProductQuantity();
            }
        } catch (Exception e) {
            log.error("对象转换失败,string={}", orderForm.getItems());
            throw new SellException(ResultEnum.PARAM_ERROR);
        }

        orderDTO.setTotalAccount(totalAccount);
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

}
