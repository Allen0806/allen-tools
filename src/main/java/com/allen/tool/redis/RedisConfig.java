package com.allen.tool.redis;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 *
 * @author Allen
 * @date 2019年12月18日
 * @since
 *
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	@ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
	@Scope(value = "prototype")
	public GenericObjectPoolConfig<?> redisPool() {
		return new GenericObjectPoolConfig<>();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.redis.redis-a")
	public RedisStandaloneConfiguration redisConfigA() {
		return new RedisStandaloneConfiguration();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.redis.redis-b")
	public RedisStandaloneConfiguration redisConfigB() {
		return new RedisStandaloneConfiguration();
	}

	@Bean
	@Primary
	public LettuceConnectionFactory factoryA(GenericObjectPoolConfig<?> config,
			RedisStandaloneConfiguration redisConfigA) {
		LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config)
				.commandTimeout(Duration.ofMillis(config.getMaxWaitMillis())).build();
		return new LettuceConnectionFactory(redisConfigA, clientConfiguration);
	}

	@Bean
	public LettuceConnectionFactory factoryB(GenericObjectPoolConfig<?> config,
			RedisStandaloneConfiguration redisConfigB) {
		LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config)
				.commandTimeout(Duration.ofMillis(config.getMaxWaitMillis())).build();
		return new LettuceConnectionFactory(redisConfigB, clientConfiguration);
	}

	@Bean(name = "redisTemplateA")
	public StringRedisTemplate redisTemplateA(LettuceConnectionFactory factoryA) {
		StringRedisTemplate template = new StringRedisTemplate(factoryA);
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		// om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setKeySerializer(redisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}

	@Bean(name = "redisTemplateB")
	public StringRedisTemplate redisTemplateB(@Autowired @Qualifier("factoryB") LettuceConnectionFactory factoryB) {
		StringRedisTemplate template = new StringRedisTemplate(factoryB);
		Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 将类名称序列化到json串中
		om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
		// om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

}
