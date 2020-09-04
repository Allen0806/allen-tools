package com.allen.tool.exception;

/**
 * 公共业务异常类
 *
 * @author Allen
 * @date Jul 17, 2020
 * @since 1.0.0
 */
public class CustomBusinessException extends RuntimeException {

	/**
	 * 异常编码
	 */
	private String statusCode;

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -7730827723657141330L;

	/**
	 * 默认构造方法
	 */
	public CustomBusinessException() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param statusCode  异常编码
	 * @param message 异常消息
	 */
	public CustomBusinessException(String statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

	/**
	 * 构造方法
	 * 
	 * @param statusCode  异常编码
	 * @param message 异常消息
	 * @param cause   其他异常对象
	 */
	public CustomBusinessException(String statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
