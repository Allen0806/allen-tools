package com.allen.tool.exception;

import com.allen.tool.result.ResultStatus;

/**
 * 公共业务异常类
 *
 * @author allen
 * @date Jul 17, 2020
 * @since 1.0.0
 */
public class CustomBusinessException extends RuntimeException {

	/**
	 * 异常编码
	 */
	private String code;

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
	 * @param resultStatus 状态码对象
	 */
	public CustomBusinessException(ResultStatus resultStatus) {
		this(resultStatus.getCode(), resultStatus.getMessage());
	}

	/**
	 * 构造方法
	 * 
	 * @param resultStatus 状态码对象
	 * @param cause        异常消息
	 */
	public CustomBusinessException(ResultStatus resultStatus, Throwable cause) {
		this(resultStatus.getCode(), resultStatus.getMessage(), cause);
	}

	/**
	 * 构造方法
	 * 
	 * @param code 异常编码
	 * @param message    异常消息
	 */
	public CustomBusinessException(String code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 构造方法
	 * 
	 * @param code 异常编码
	 * @param message    异常消息
	 * @param cause      其他异常对象
	 */
	public CustomBusinessException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	@Deprecated
	public String getStatusCode() {
		return code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
