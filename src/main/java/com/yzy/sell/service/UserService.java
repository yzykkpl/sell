package com.yzy.sell.service;

import com.yzy.sell.dataobject.UserInfo;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-03 17:53
 */
public interface UserService {
    UserInfo findUserByOpenid(String openid);
}
