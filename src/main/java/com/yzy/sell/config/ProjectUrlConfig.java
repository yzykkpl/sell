package com.yzy.sell.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: Url配置
 * @author: yzy
 * @create: 2018-04-03 14:37
 */
@Data
@ConfigurationProperties(prefix = "projectUrl")
public class ProjectUrlConfig {
}
