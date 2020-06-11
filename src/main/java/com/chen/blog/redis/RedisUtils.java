package com.chen.blog.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisUtils {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	private static final Long SUCCESS = 1L;

	protected static final Logger log = LoggerFactory.getLogger(RedisUtils.class);

	/**
	 * redis分布式锁/获取锁
	 * 
	 * @param lockKey
	 * @param value
	 * @param expireTime：单位-秒
	 * @return
	 */
	public boolean getLock(String lockKey, String value, String expireTime) throws Exception {
		String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
		RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value, expireTime);
		if (SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	// 防止redisTemplate 乱码
	@Autowired(required = false)
	public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer);
		this.redisTemplate = redisTemplate;
	}

	/**
	 * redis分布式锁/释放锁
	 * 
	 * @param lockKey
	 * @param value
	 * @return
	 */
	public boolean releaseLock(String lockKey, String value) throws Exception {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
		if (SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	public void setKey(String key, String value) {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set(key, value);
	}

	public void setKeyAndTimeOut(String key, long timeout, String value) {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set(key, value);
		ops.set(key, value, timeout, TimeUnit.SECONDS);
	}

	public String getValue(String key) {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		return ops.get(key);
	}

	/**
	 * 
	 * @Title: hasKey
	 * @Description: 检查key是否存在
	 * @param @param
	 *            key
	 * @param @return
	 *            设定文件
	 * @return boolean 返回类型
	 */
	public boolean hasKey(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	/**
	 * 
	 * @Title: expire
	 * @Description: 设置key的过期时间
	 * @param @param
	 *            key
	 * @param @param
	 *            seconds 单位：秒
	 * @param @return
	 *            设定文件
	 * @return boolean 返回类型
	 */
	public boolean expire(String key, long seconds) {
		return stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}

	/**
	 * 
	 * @Title: increment
	 * @Description: 使key增加(仅适用于value为数字的key)
	 * @param @param
	 *            key
	 * @param @param
	 *            delta 增加量
	 * @param @return
	 *            设定文件
	 * @return Long 返回类型
	 */
	public Long increment(String key, long delta) {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		Long id = ops.increment(key, delta);
		return id;
	}

	/**
	 * 
	 * @Title: getKeys
	 * @Description: 获取key集合 参数后需加*，例：如需获取1-1,1-2,1-3。则key为： 1-*
	 * @param @param
	 *            key
	 * @param @return
	 *            设定文件
	 * @return Set<String> 返回类型
	 */
	public Set<String> getKeys(String key) {
		Set<String> ops = stringRedisTemplate.keys(key);
		return ops;
	}

	/**
	 * 
	 * @Title: delKey
	 * @Description: 删除key及value
	 * @param @param
	 *            key 设定文件
	 * @return void 返回类型
	 */
	public void delKey(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * 
	 * @Title: delKeys
	 * @Description: 删除keys
	 * @param @param
	 *            keys 设定文件
	 * @return void 返回类型
	 */
	public void delKeys(String... keys) {
		List<String> list = new ArrayList<String>();
		for (String key : keys) {
			list.add(key);
		}
		stringRedisTemplate.delete(list);
	}
	
	
	/**
	 * @Title: delKeysByKeyName
	 * @Description: 根据keyName查找匹配的对象然后删除,如果是带*的那么就是模糊删除
	 * @param keyName 删除key的名称
	 * @return: void
	 */
	public void delKeysByKeyName(String keyName) {
		Set<String> keys = stringRedisTemplate.keys(keyName);
		for (String key : keys) {
			stringRedisTemplate.delete(key);
		}
	}
	
	

	/**
	 * 
	 * @Title: getExpire
	 * @Description: 获取key的失效时间
	 * @param @param
	 *            key 设定文件
	 * @return
	 * @return void 返回类型
	 */
	public Long getExpire(String key) {
		return stringRedisTemplate.getExpire(key);
	}

	public String getKeyAndSetValue(String key, String value) {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		return ops.getAndSet(key, value);
	}

	public void setKey(String redisKey, String mapKey, String mapValue) {
		HashOperations<Object, Object, Object> ops = redisTemplate.opsForHash();
		ops.put(redisKey, mapKey, mapValue);
	}

	public Object getMap(String redisKey, String mapKey) {
		HashOperations<Object, Object, Object> ops = redisTemplate.opsForHash();
		return ops.get(redisKey, mapKey);
	}

	public void setKeyLeftPush(String redisKey, Object indexValue) {
		ListOperations<Object, Object> ops = redisTemplate.opsForList();
		ops.leftPush(redisKey, indexValue);// 在左边添加
	}

	public void setKeyRightPush(String redisKey, Object indexValue) {
		ListOperations<Object, Object> ops = redisTemplate.opsForList();
		ops.rightPush(redisKey, indexValue);
	}

	public void setKeyRightAndLeftPush(String redisKey, Object indexValue) {
		ListOperations<Object, Object> ops = redisTemplate.opsForList();
		ops.rightPopAndLeftPush(redisKey, indexValue);
	}

	public void setKey(Object key, Object value) {
		ValueOperations<Object, Object> ops = redisTemplate.opsForValue();
		// redisTemplate.opsForSet();//操作set
		// redisTemplate.opsForZSet();//操作有序set
		ops.set(key, value);
	}

	public Object getValue(Object key) {
		ValueOperations<Object, Object> ops = redisTemplate.opsForValue();
		return ops.get(key);
	}
}
