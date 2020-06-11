package com.chen.blog.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 表名主键配置类，用以获取初始化主键
 *
 * 暂时只用来生成唯一登录账号，其它主键使用数据库自增
 *
 */
@Data
@Component
@ConfigurationProperties(prefix="dbid")
@PropertySource("classpath:dbidconfig.properties")
public class DBIdConfig {
	private String account;
}
