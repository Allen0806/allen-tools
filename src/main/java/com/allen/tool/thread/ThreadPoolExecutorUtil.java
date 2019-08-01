package com.allen.tool.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThreadPoolExecutorUtil {

	/**
	 * 日志工具
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolExecutorUtil.class);

	/**
	 * 线程执行者
	 */
	private static volatile ThreadPoolExecutor executorService;

	/**
	 * 获取线程执行服务
	 * 
	 * @return 线程执行服务
	 */
	public static ThreadPoolExecutor getExecutorService() {
		if (executorService == null || executorService.isShutdown()) {
			synchronized (ThreadPoolExecutorUtil.class) {
				if (executorService == null || executorService.isShutdown()) {
					init();
				}
			}
		}
		return executorService;
	}

	/**
	 * 禁止实例化
	 */
	private ThreadPoolExecutorUtil() {

	}

	/**
	 * 线程池初始化，默认创建30个线程，最大50个
	 */
	private static void init() {
		executorService = new ThreadPoolExecutor(
				// 核心线程数量
				30,
				// 最大线程数量
				50,
				// 当线程空闲时，保持活跃的时间
				1L,
				// 时间单元 ，毫秒级
				TimeUnit.SECONDS,
				// 线程任务队列
				new LinkedBlockingQueue<Runnable>(),
				// 创建线程的工厂
				Executors.defaultThreadFactory());

		// JVM停止或重启时，关闭连接池释放掉连接
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					executorService.shutdown();
					executorService.awaitTermination(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					LOGGER.error("关闭线程连接池错误", e);
				}
			}
		});
	}
}
