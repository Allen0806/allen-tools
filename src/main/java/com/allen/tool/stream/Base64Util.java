package com.allen.tool.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen.tool.string.StringUtil;

/**
 * Base64编码工具
 *
 * @author luoxuetong
 * @date 2020年5月9日
 * @since 1.0.0
 *
 */
public class Base64Util {

	private static final Logger LOGGER = LoggerFactory.getLogger(Base64Util.class);

	/**
	 * 禁止实例化
	 */
	private Base64Util() {

	}

	/**
	 * 将给定的文件转为Base64编码
	 * 
	 * @param filePath 文件绝对路径，包含文件名
	 * @return 转换后的Base64编码
	 */
	public static String toBase64(String filePath) {
		if (StringUtil.isBlank(filePath)) {
			LOGGER.error("文件转为Base64编码失败，给定的文件名为空");
			return null;
		}

		return toBase64(new File(filePath));
	}

	/**
	 * 将给定的文件转为Base64编码
	 * 
	 * @param file 文件对象
	 * @return 转换后的Base64编码
	 */
	public static String toBase64(File file) {
		if (file == null) {
			LOGGER.error("文件转为Base64编码失败，给定的文件对象为空");
			return null;
		}
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(file);
			data = new byte[in.available()];
			in.read(data);
			// 对字节数组Base64编码，并返回
			return Base64.getEncoder().encodeToString(data);
		} catch (IOException e) {
			LOGGER.error("文件转为Base64编码失败，文件名为：{}", file.getName(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("文件转为Base64编码失败，关闭流时发生错误，文件名为：{}", file.getName(), e);
				}
			}
		}
		return null;
	}

	/**
	 * 将给定的Base64编码输出为文件
	 * 
	 * @param base64   Base64编码
	 * @param filePath 输出的文件绝对路径，包含文件名
	 * @return 转换是否成功
	 */
	public static boolean toFile(String base64, String filePath) {
		if (StringUtil.isBlank(base64)) {
			LOGGER.error("Base64编码转文件失败，给定的Base64编码为空，文件路径为：{}", filePath);
			return false;
		}

		if (StringUtil.isBlank(filePath)) {
			LOGGER.error("Base64编码转文件失败，给定的文件路径为空");
			return false;
		}
		OutputStream out = null;
		try {
			// Base64解码
			byte[] byteArray = Base64.getDecoder().decode(base64);
			for (int i = 0; i < byteArray.length; ++i) {
				if (byteArray[i] < 0) {
					// 调整异常数据
					byteArray[i] += 256;
				}
			}
			out = new FileOutputStream(filePath);
			out.write(byteArray);
			out.flush();
			return true;
		} catch (Exception e) {
			LOGGER.error("Base64编码输出文件失败，文件为：{}", filePath, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("Base64编码输出文件失败，关闭流时发生错误，文件名为：{}", filePath, e);
				}
			}
		}
		return false;
	}
}
