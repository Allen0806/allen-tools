package com.allen.tool.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具
 *
 * @author allen
 * @date 2020年5月9日
 * @since 1.0.0
 *
 */
public final class ThreadPoolExecutorUtil {

	/**
	 * 存储线程池名称对应的线程池
	 */
	private static Map<String, ThreadPoolExecutor> executorMap = new ConcurrentHashMap<>(16);

	/**
	 * 获取线程池对象
	 * 
	 * @param executorName 线程池名称
	 * @return 线程池对象
	 */
	public static ThreadPoolExecutor getExecutor(String executorName) {
		ThreadPoolExecutor executor = executorMap.get(executorName);
		if (executor == null) {
			synchronized (ThreadPoolExecutorUtil.class) {
				executor = executorMap.get(executorName);
				if (executor == null) {
					executor = new ThreadPoolExecutor(30, 50, 5L, TimeUnit.SECONDS,
							new LinkedBlockingQueue<Runnable>(100), new CustomizableThreadFactory(executorName),
							new ThreadPoolExecutor.AbortPolicy());
					executorMap.put(executorName, executor);
				}
			}
		}
		return executor;
	}
	
	/**
	 * 禁止实例化
	 */
	private ThreadPoolExecutorUtil() {

	}
}
