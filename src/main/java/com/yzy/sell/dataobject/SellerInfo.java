package com.yzy.sell.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @description: 卖家信息表
 * @author: yzy
 * @create: 2018-04-01 16:04
 */
@Entity
@Data
public class SellerInfo {

    @Id
    private String sellerId;

    private String username;

    private String password;

    private String openid;
}
