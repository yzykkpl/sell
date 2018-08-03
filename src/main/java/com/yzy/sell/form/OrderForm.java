package com.yzy.sell.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


/**
 * @description: 订单表单
 * @author: yzy
 * @create: 2018-03-26 18:55
 */
@Data
public class OrderForm {

    @NotEmpty(message = "姓名必填")
    private String name;

    @NotEmpty(message = "手机必填")
    private String phone;

    @NotEmpty(message = "地址必填")
    private String address;

    private String openid;

    @NotEmpty(message = "购物车不能为空")
    private String items;

    @NotEmpty(message = "token必填")
    private String token;
}
