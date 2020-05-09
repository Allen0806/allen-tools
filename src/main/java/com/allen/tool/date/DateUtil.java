package com.allen.tool.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.allen.tool.string.StringUtil;

/**
 * 日期型工具类，包含所有日期格式相关的处理方法
 * 
 * @author Allen
 * @since 1.0.0
 */
public final class DateUtil {
	// TODO 增加新的日期处理

	/**
	 * 禁止实例化
	 */
	private DateUtil() {

	}

	/**
	 * 将给定格式的日期字符串转换为Date对象
	 * 
	 * @param dateStr    日期字符串
	 * @param dateFormat 日期格式字符串
	 * @return 转换后的Date对象
	 * @throws IllegalArgumentException 如果日期字符串或日期格式字符串为空 ，或者日期格式字符串不合法
	 * @throws ParseException           如果不能解析日期字符串
	 * @author Allen
	 * @date 2018年5月18日 下午8:08:30
	 */
	public static Date toDate(String dateStr, String dateFormat) throws ParseException {
		if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(dateFormat)) {
			throw new IllegalArgumentException("日期字符串及日期格式字符串不能为空");
		}
		SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
		return formater.parse(dateStr);
	}

	/**
	 * 将给定的日期对象转换为指定格式的日期字符串
	 * 
	 * @param date       日期对象
	 * @param dateFormat 日期格式字符串
	 * @return 格式化后的日期字符串
	 * @throws IllegalArgumentException 如果日期对象或日期格式字符串为空 ，或者日期格式字符串不合法
	 * @author Allen
	 * @date 2018年5月18日 下午8:08:30
	 */
	public static String toString(Date date, String dateFormat) {
		if (date == null || StringUtil.isEmpty(dateFormat)) {
			throw new IllegalArgumentException("日期对象及日期格式字符串不能为空");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(date);
	}

	/**
	 * 转换为Calendar对象
	 * 
	 * @param date 日期对象
	 * @return Calendar对象
	 */
	public static Calendar toCalendar(Date date) {
		return toCalendar(date.getTime());
	}

	/**
	 * 转换为Calendar对象
	 * 
	 * @param millis 时间戳
	 * @return Calendar对象
	 */
	public static Calendar toCalendar(long millis) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return cal;
	}

	/**
	 * 校验给定的字符串是否满足指定的日期格式
	 * 
	 * @param dateStr    日期字符串
	 * @param dateFormat 日期格式
	 * @return true-满足；false-不满足
	 * @author lxt
	 * @since 1.0
	 */
	public static boolean isValidDate(String dateStr, String dateFormat) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try {
			// 设置lenient为false.
			// 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			format.setLenient(false);
			format.parse(dateStr);
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}
}
