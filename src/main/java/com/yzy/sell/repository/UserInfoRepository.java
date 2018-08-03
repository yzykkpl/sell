package com.yzy.sell.repository;

import com.yzy.sell.dataobject.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description: 用户信息表
 * @author: yzy
 * @create: 2018-04-03 17:52
 */
public interface UserInfoRepository extends JpaRepository<UserInfo,Integer>{
    UserInfo findByOpenid(String openid);
}
