package com.allen.tool.redis;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis分布式锁
 *
 * @author Allen
 * @date 2020年10月28日
 * @since 1.0.0
 */
@Component
@Slf4j
public class RedisLock {

	/**
	 * redisTemplate实例
	 */
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 释放锁脚本
	 */
	private static final String UNLOCK_LUA;

	/**
	 * 释放锁脚本，原子操作
	 */
	static {
		StringBuilder sb = new StringBuilder();
		sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
		sb.append("then ");
		sb.append("return redis.call(\"del\",KEYS[1]) ");
		sb.append("else ");
		sb.append("return 0 ");
		sb.append("end ");
		UNLOCK_LUA = sb.toString();
	}

	/**
	 * 获取分布式锁
	 * 
	 * @param lockKey    加锁的key
	 * @param requestId  唯一ID, 可以为UUID或线程ID;
	 * @param expireTime 过期时间
	 * @param timeUnit   时间单位
	 * @return true/false
	 */
	public boolean tryLock(String lockKey, String requestId, long expireTime, TimeUnit timeUnit) {
		try {
			RedisCallback<Boolean> callback = (connection) -> {
				return connection.set(lockKey.getBytes(Charset.forName("UTF-8")),
						requestId.getBytes(Charset.forName("UTF-8")),
						Expiration.milliseconds(timeUnit.toMillis(expireTime)),
						RedisStringCommands.SetOption.SET_IF_ABSENT);
			};
			return redisTemplate.execute(callback);
		} catch (Exception e) {
			log.error("获取锁异常，key为：{}，请求ID为：{}", lockKey, requestId, e);
		}
		return false;
	}

	/**
	 * 获取分布式锁，原子操作
	 * 
	 * @param lockKey    加锁的key
	 * @param requestId  唯一ID, 可以为UUID或线程ID;
	 * @param waitTime   等待时间
	 * @param expireTime 过期时间
	 * @param timeUnit   时间单位
	 * @return true/false
	 */
	public boolean tryLock(String lockKey, String requestId, long waitTime, long expireTime, TimeUnit timeUnit) {
		long time = timeUnit.toMillis(waitTime);
		long current = System.currentTimeMillis();
		do {
			boolean locked = tryLock(lockKey, requestId, expireTime, timeUnit);
			if (locked) {
				return locked;
			}
			time -= System.currentTimeMillis() - current;
			if (time <= 0) {
				return false;
			}
			current = System.currentTimeMillis();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("线程Sleep异常，key为：{}，请求ID为：{}", lockKey, requestId, e);
			}
		} while (true);
	}

	/**
	 * 释放锁
	 * 
	 * @param lockKey   加锁的key
	 * @param requestId 唯一ID, 可以为UUID或线程ID;
	 * @return true/false
	 */
	public boolean unLock(String lockKey, String requestId) {
		try {
			RedisCallback<Boolean> callback = (connection) -> {
				return connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1,
						lockKey.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")));
			};
			return redisTemplate.execute(callback);
		} catch (Exception e) {
			log.error("释放锁异常，key为：{}，请求ID为：{}", lockKey, requestId, e);
		}
		return false;
	}

	/**
	 * 获取Redis锁的value值
	 * 
	 * @param lockKey 加锁的key
	 * @return 加锁的值
	 */
	public String get(String lockKey) {
		try {
			// 使用下面的方法可以么？
			// return (String) redisTemplate.opsForValue().get(lockKey);
			RedisCallback<String> callback = (connection) -> {
				return new String(connection.get(lockKey.getBytes()), Charset.forName("UTF-8"));
			};
			return redisTemplate.execute(callback);
		} catch (Exception e) {
			log.error("获取锁的值异常，锁key为：{}", lockKey, e);
		}
		return null;
	}

}
