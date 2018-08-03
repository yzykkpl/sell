package com.yzy.sell.service.impl;

import com.yzy.sell.dataobject.SellerInfo;
import com.yzy.sell.repository.SellerInfoRepository;
import com.yzy.sell.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-01 16:40
 */
@Service
public class SellerServiceImpl implements SellerService{
    @Autowired
    private SellerInfoRepository repository;

    @Override
    public SellerInfo findSellerInfoByOpenid(String openid) {
        return repository.findByOpenid(openid);
    }
}
