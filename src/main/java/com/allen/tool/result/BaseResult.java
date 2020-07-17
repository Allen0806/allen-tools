package com.allen.tool.result;

public class BaseResult<T> {

	/**
	 * 成功时对应的状态码
	 */
	public static final String STATUS_HANDLE_SUCCESS = "000000";

	/**
	 * 系统异常时对应的状态码
	 */
	public static final String STATUS_SYSTEM_FAILURE = "999999";
	
	/**
	 * 参数校验异常时对应的状态码
	 */
	public static final String STATUS_VALIDATION_FAILURE = "999998";

	/**
	 * 状态码
	 */
	private String status;

	/**
	 * 状态信息
	 */
	private String message;

	/**
	 * 业务对象
	 */
	private T data;

	/**
	 * 构造方法，默认初始化为成功状态
	 */
	public BaseResult() {
		this(STATUS_HANDLE_SUCCESS, null, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param status  状态码
	 * @param message 状态信息
	 */
	public BaseResult(String status, String message) {
		this(status, message, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param status  状态码
	 * @param message 状态信息
	 * @param data    结果对象
	 */
	public BaseResult(String status, String message, T data) {
		this.status = status;
		this.data = data;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	 * 判断是否处理成功
	 * 
	 * @return 处理结果
	 */
	public boolean success() {
		return STATUS_HANDLE_SUCCESS.equals(status);
	}

}
