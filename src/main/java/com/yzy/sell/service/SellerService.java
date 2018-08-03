package com.yzy.sell.service;

import com.yzy.sell.dataobject.SellerInfo;

/**
 * @description: 卖家端
 * @author: yzy
 * @create: 2018-04-01 16:38
 */
public interface SellerService {

    /**
     * 通过openid查找卖家用户
     * @param:
     */
    SellerInfo findSellerInfoByOpenid(String openid);
}
