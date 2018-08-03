package com.yzy.sell.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yzy
 * @create: 2018-05-19 01:17
 */
@Data
public class SimuOrderDTO {
    private String detailId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;
    /** 商品名称. */
    private String productName;
    /** 商品数量. */
    private Integer productQuantity;
    /** 创建时间. */
    private Date createTime;

    /** 更新时间. */
    private Date updateTime;

}
