package com.chen.blog.utils;

import com.chen.blog.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 主键生成工具类
 */
@Slf4j
@Component("dbIdGenerate")
public class DBIdGenerate {

	@Autowired
	public RedisUtils redisUtils;
	@Autowired
	public DBIdConfig dBIdConfig;

	/**
	 * 获取主键ID
	 * @param key
	 * @param value
	 * @return
	 */
    public  Long doGetDBId(String key,String value) {
    	if(!redisUtils.hasKey(key)) {
    		redisUtils.setKey(key, value);
		}
		return redisUtils.increment(key, 1);
    }
	
}