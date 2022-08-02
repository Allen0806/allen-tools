package com.allen.tool.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可定义的线程工厂
 *
 * @author allen
 * @date Aug 26, 2020
 * @since 1.0.0
 */
public class CustomizableThreadFactory implements ThreadFactory {

	/**
	 * 线程命名前缀
	 */
	private final String namePrefix;

	/**
	 * 线程编号
	 */
	private final AtomicInteger nextId = new AtomicInteger(1);
	
	/**
	 * 构造方法
	 * 
	 * @param threadGroupName 线程组名称
	 */
	public CustomizableThreadFactory(String threadGroupName) {
		namePrefix = "CustomizableThreadFactory-" + threadGroupName + "-Worker-";
	}

	@Override
	public Thread newThread(Runnable task) {
		String name = namePrefix + nextId.getAndIncrement();
		Thread thread = new Thread(null, task, name);
		return thread;
	}

}
