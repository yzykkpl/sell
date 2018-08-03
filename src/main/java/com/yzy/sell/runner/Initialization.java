package com.yzy.sell.runner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yzy.sell.config.WxConfig;
import com.yzy.sell.constant.TokenConstant;
import com.yzy.sell.utils.AccessTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 启动初始化类
 * @author: yzy
 * @create: 2018-04-03 15:15
 */
@Component
@Slf4j
public class Initialization implements ApplicationRunner{
    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
