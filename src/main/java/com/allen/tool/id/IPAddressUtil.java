package com.allen.tool.id;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取本机IP、Mac地址等工具类
 *
 * @author Allen
 * @date 2020年5月9日
 * @since 1.0.0
 *
 */
public class IPAddressUtil {

	/**
	 * 日志工具
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IPAddressUtil.class);

	/**
	 * 获取本机IP地址
	 * 
	 * @return 本机IP地址
	 */
	public static String getLocalHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			LOGGER.error("获取本机IP地址发生异常", e);
		}
		return null;
	}

	/**
	 * 获取本机Mac地址
	 * 
	 * @return 本机Mac地址
	 */
	public static String getMacAddress() {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			// 下面代码是把mac地址拼装成String
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				System.out.println("mac:" + mac[i]);
				// mac[i] & 0xFF 是为了把byte转化为正整数
				String s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
			// 把字符串所有小写字母改为大写成为正规的mac地址并返回
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			LOGGER.error("获取本机Mac地址发生异常", e);
		}
		return null;
	}
}
