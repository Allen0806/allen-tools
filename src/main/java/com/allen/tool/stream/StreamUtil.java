package com.allen.tool.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Java的流处理工具类
 * 
 * @author lxt
 * @since 1.0
 *
 */
public class StreamUtil {

	/**
	 * 禁止实例化
	 */
	private StreamUtil() {

	}

	/**
	 * 将InputStream转换为byte数组
	 * 
	 * @param input InputStream对象
	 * @return bytes数组
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	/**
	 * 将输入流拷贝到输出流中
	 * 
	 * @param input  输入流
	 * @param output 输出流
	 * @return 拷贝的字节数
	 * @throws IOException
	 */
	public static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
