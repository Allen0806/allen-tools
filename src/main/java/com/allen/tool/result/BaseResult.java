package com.allen.tool.result;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseResult<T> {

	/**
	 * 状态码
	 */
	private String statusCode;

	/**
	 * 状态信息
	 */
	private String message;

	/**
	 * 业务对象
	 */
	private T data;

	/**
	 * 成功时的结果对象
	 * 
	 * @param <T> 需要返回的业务对象类型
	 * @return 结果对象
	 */
	public static <T> BaseResult<T> success() {
		return new BaseResult<>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMessage());
	}

	/**
	 * 成功时的结果对象
	 * 
	 * @param <T>  需要返回的业务对象类型
	 * @param data 需要返回的业务对象
	 * @return 结果对象
	 */
	public static <T> BaseResult<T> success(T data) {
		return new BaseResult<>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMessage(), data);
	}

	/**
	 * 系统异常时的结果对象
	 * 
	 * @param <T>  需要返回的业务对象类型
	 * @param data 需要返回的业务对象
	 * @return 结果对象
	 */
	public static <T> BaseResult<T> systemError(T data) {
		return new BaseResult<>(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getMessage(), data);
	}

	/**
	 * 参数错误时的结果对象
	 * 
	 * @param <T>  需要返回的业务对象类型
	 * @param data 需要返回的业务对象
	 * @return 结果对象
	 */
	public static <T> BaseResult<T> paramError(T data) {
		return new BaseResult<>(StatusCode.PARAM_ERROR.getCode(), StatusCode.PARAM_ERROR.getMessage(), data);
	}

	/**
	 * 构造方法，默认初始化为成功状态
	 */
	public BaseResult() {

	}

	/**
	 * 构造方法
	 * 
	 * @param status  状态码
	 * @param message 状态信息
	 */
	public BaseResult(String statusCode, String message) {
		this(statusCode, message, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param status  状态码
	 * @param message 状态信息
	 * @param data    结果对象
	 */
	public BaseResult(String statusCode, String message, T data) {
		this.statusCode = statusCode;
		this.data = data;
		this.message = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	/**
	 * 判断是否成功。@JsonIgnore注解表示使用Jackson时不对该方法进行处理，否则转换为Json时会带上："successful":true
	 * 
	 * @return 处理结果
	 */
	@JsonIgnore
	public boolean isSuccessful() {
		return StatusCode.SUCCESS.getCode().equals(statusCode);
	}
}
