package com.allen.tool.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 根据Bean的名称获取Spring容器中的bean实例
 * 
 * @author luoxuetong
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取Bean实例
	 * 
	 * @param <T>
	 * @param clazz 类对象
	 * @return bean实例
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext != null ? applicationContext.getBean(clazz) : null;
	}

	/**
	 * 获取Bean实例
	 * 
	 * @param <T>
	 * @param beanName bean名称
	 * @param clazz    类对象
	 * @return bean实例
	 */
	public static <T> T getBean(String beanName, Class<T> clazz) {
		return applicationContext != null ? applicationContext.getBean(beanName, clazz) : null;
	}
}
