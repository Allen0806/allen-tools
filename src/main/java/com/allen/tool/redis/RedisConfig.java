package com.allen.tool.redis;

import java.time.Duration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis工具类
 * 
 * @author Allen
 * @date 2019年12月18日
 * @since 1.0.0
 *
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	/**
	 * 创建redisTemplate单实例
	 * 
	 * @param redisConnectionFactory redis连接工厂，默认为LettuceConnectionFactory
	 * @return redisTemplate单实例
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisSerializer<Object> serializer = redisSerializer();
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(serializer);
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(serializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	/**
	 * 创建redis缓存控制器
	 * 
	 * @param redisConnectionFactory redis连接工厂，默认为LettuceConnectionFactory
	 * @return redis缓存控制器
	 */
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		// 设置Redis缓存有效期为1天
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()))
				.entryTtl(Duration.ofDays(1));
		return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
	}

	/**
	 * 创建redis序列化器
	 * 
	 * @return redis序列化器对象
	 */
	private RedisSerializer<Object> redisSerializer() {
		// 创建JSON序列化器
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 将类名称序列化到json串中
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.NON_FINAL);
		serializer.setObjectMapper(objectMapper);
		return serializer;
	}

}
