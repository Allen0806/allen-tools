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
	private String status;

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
	 * @param status  异常编码
	 * @param message 异常消息
	 */
	public CustomBusinessException(String status, String message) {
		super(message);
		this.status = status;
	}

	/**
	 * 构造方法
	 * 
	 * @param status  异常编码
	 * @param message 异常消息
	 * @param cause   其他异常对象
	 */
	public CustomBusinessException(String status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
