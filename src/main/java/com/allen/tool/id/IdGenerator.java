package com.allen.tool.id;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;

/**
 * 唯一流水号生成器，参考雪花算法，生成规则：当前时间（17位，yyyyMMddHHmmssSSS)+Mac地址拼装（最多18位）+序列号（最多4位）
 * 
 * @author Allen
 *
 */
public enum IdGenerator {

	/**
	 * 唯一实例
	 */
	INSTANCE;

	/**
	 * 本机Mac地址的拼装
	 */
	private String workerId;

	/**
	 * 当前毫秒内的序列
	 */
	private long sequence = 0L;

	/**
	 * 序列号最大位数
	 */
	private long sequenceBits = 12L;

	/**
	 * 序列号最大值，4096
	 */
	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	/**
	 * 上一次获取的毫秒时间
	 */
	private long lastTimestamp = -1L;

	private IdGenerator() {
		workerId = getWorkerId();
	}

	/**
	 * 生成下一个唯一ID
	 * 
	 * @return 下一个唯一ID
	 */
	public synchronized String nextId() {
		// 获取当前毫秒数
		long timestamp = timeGen();
		// 如果服务器时间有问题(时钟后退) 报错。
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format("时钟后退了，在 %d 毫秒内禁止生成ID", lastTimestamp - timestamp));
		}
		// 如果上次生成时间和当前时间相同,在同一毫秒内
		if (lastTimestamp == timestamp) {
			// sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
			sequence = (sequence + 1) & sequenceMask;
			// 判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
			if (sequence == 0) {
				// 自旋等待到下一毫秒
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			// 如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
			sequence = 0L;
		}
		lastTimestamp = timestamp;

		String datePrefix = new SimpleDateFormat("yyyyMMddHHMMssSSS").format(timestamp);
		return datePrefix + workerId + sequence;
	}

	/**
	 * 获取实际使用的毫秒数，如果与上一次相同，则等待获取下一个时间毫秒数
	 * 
	 * @param lastTimestamp 上一次获取的时间毫秒数
	 * @return 实际使用的毫秒数
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return 当前时间
	 */
	private long timeGen() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取本机Mac地址的拼装，由数值拼装的字符串
	 * 
	 * @return 本机Mac地址的数值拼装的字符串
	 */
	private String getWorkerId() {
		try {
			// 获取本地IP对象
			InetAddress ia = InetAddress.getLocalHost();
			// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

			// 把mac地址拼装成String
			StringBuffer sBuffer = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				sBuffer.append(mac[i] & 0xFF);
			}
			return sBuffer.toString();
		} catch (Exception e) {
			throw new RuntimeException("获取本机Mac地址失败", e);
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			System.out.println(IdGenerator.INSTANCE.nextId());
		}
		System.out.println("time spend : " + (System.currentTimeMillis() - start));
	}

}
