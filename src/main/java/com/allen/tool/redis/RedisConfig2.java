package com.allen.tool.redis;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.allen.tool.string.StringUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 初始化redis，与运营平台使用相同redis
 * 
 * @author Allen
 * @date 2019年12月18日
 * @since 1.0.0
 *
 */
@Configuration
@EnableCaching
public class RedisConfig2 extends CachingConfigurerSupport {

	/**
	 * redis服务器地址
	 */
	@Value("${fincloud.portal.redis.host}")
	private String hostName;

	/**
	 * redis服务器端口号
	 */
	@Value("${fincloud.portal.redis.port}")
	private int port;

	/**
	 * redis连接密码
	 */
	@Value("${fincloud.portal.redis.password}")
	private String password;

	/**
	 * redis数据库索引
	 */
	@Value("${fincloud.portal.redis.database}")
	private int database;

	/**
	 * 连接超时时间，如果为0表示不设置，采用系统默认值60秒
	 */
	@Value("${fincloud.portal.redis.timeout:0}")
	private long timeout;

	/**
	 * 连接池最大连接数
	 */
	@Value("${fincloud.portal.redis.lettuce.pool.max-active}")
	private int maxActive;

	/**
	 * 连接池最大阻塞等待时间（使用负值表示没有限制）
	 */
	@Value("${fincloud.portal.redis.lettuce.pool.max-wait:-1}")
	private int maxWait;

	/**
	 * 连接池最大空闲连接数
	 */
	@Value("${fincloud.portal.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	/**
	 * 连接池最小空闲连接数
	 */
	@Value("${fincloud.portal.redis.lettuce.pool.min-idle}")
	private int minIdle;

	/**
	 * 创建RedisTemplate实例
	 * 
	 * @return RedisTemplate实例
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate4Portal() {

		/* ========= 基本配置 ========= */
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(hostName);
		configuration.setPort(port);
		configuration.setDatabase(database);
		if (StringUtil.isNotBlank(password)) {
			RedisPassword redisPassword = RedisPassword.of(password);
			configuration.setPassword(redisPassword);
		}

		/* ========= 连接池通用配置 ========= */
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
		genericObjectPoolConfig.setMaxTotal(maxActive);
		genericObjectPoolConfig.setMinIdle(minIdle);
		genericObjectPoolConfig.setMaxIdle(maxIdle);
		if (maxWait > -1) {
			genericObjectPoolConfig.setMaxWaitMillis(maxWait);
		}

		/* ========= jedis pool ========= */
		/*
		 * JedisClientConfiguration.DefaultJedisClientConfigurationBuilder builder =
		 * (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder)
		 * JedisClientConfiguration .builder();
		 * builder.connectTimeout(Duration.ofSeconds(timeout)); builder.usePooling();
		 * builder.poolConfig(genericObjectPoolConfig); JedisConnectionFactory
		 * connectionFactory = new JedisConnectionFactory(configuration,
		 * builder.build()); // 连接池初始化 connectionFactory.afterPropertiesSet();
		 */

		/* ========= lettuce pool ========= */
		LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration
				.builder();
		builder.poolConfig(genericObjectPoolConfig);
		if (timeout > 0) {
			builder.commandTimeout(Duration.ofSeconds(timeout));
		}
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration, builder.build());
		connectionFactory.afterPropertiesSet();

		/* ========= 创建 template ========= */
		return createRedisTemplate(connectionFactory);
	}

	/**
	 * json 实现 redisTemplate
	 * <p>
	 * 该方法不能加 @Bean 否则不管如何调用，connectionFactory都会是默认配置
	 *
	 * @param redisConnectionFactory
	 * @return
	 */
	public RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.NON_FINAL);
		// objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
