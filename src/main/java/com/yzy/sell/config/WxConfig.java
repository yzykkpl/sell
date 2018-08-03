package com.yzy.sell.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-03 15:18
 */
@ConfigurationProperties(prefix = "weixin")
@Component
@Data
public class WxConfig{
    private String appSecret;

    private String appId;

    private String mchId;

    private String key;

    private String certPath;

    private String notifyUrl;



}
