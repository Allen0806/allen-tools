package com.allen.tool.result;

/**
 * 常用状态码常量类
 * 
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
public class StatusCode {

	/**
	 * 成功时对应的状态码常量
	 */
	public static final StatusCode SUCCESS = new StatusCode("000000", "成功");

	/**
	 * 系统异常对应的状态码常量
	 */
	public static final StatusCode SYSTEM_ERROR = new StatusCode("999999", "系统异常");
	
	/**
	 * 参数错误对应的状态码常量
	 */
	public static final StatusCode PARAM_ERROR = new StatusCode("999998", "参数错误");

	/**
	 * 状态编码
	 */
	private String code;

	/**
	 * 对应默认状态信息
	 */
	private String message;

	public StatusCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
