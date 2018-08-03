package com.yzy.sell.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @description: 用户信息表
 * @author: yzy
 * @create: 2018-04-03 17:48
 */
@Data
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String openid;

    private String nickName;

    public UserInfo() {
    }
}
