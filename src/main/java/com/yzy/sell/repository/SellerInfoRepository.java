package com.yzy.sell.repository;

import com.yzy.sell.dataobject.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @description:
 * @author: yzy
 * @create: 2018-04-01 16:06
 */
public interface SellerInfoRepository extends JpaRepository<SellerInfo,String> {
    SellerInfo findByOpenid(String openid);
}
