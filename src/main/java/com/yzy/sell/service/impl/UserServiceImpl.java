package com.yzy.sell.service.impl;

import com.yzy.sell.dataobject.UserInfo;
import com.yzy.sell.repository.UserInfoRepository;
import com.yzy.sell.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-03 17:55
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoRepository repository;


    @Override
    public UserInfo findUserByOpenid(String openid) {
        UserInfo userInfo=repository.findByOpenid(openid);
        if(userInfo==null)
            return null;
        return userInfo;
    }

    public UserInfo create(String openid,String name){
        UserInfo userInfo=new UserInfo();
        userInfo.setOpenid(openid);
        UserInfo result=repository.save(userInfo);
        if(result==null){
            log.error("用户创建失败，openid={},name={}",openid,name);
        }
        return result;
    }
}
