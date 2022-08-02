package com.allen.tool.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类，包含所有的字符串处理方法
 * 
 * @author luoxuetong
 * @since 1.0.0
 */
public class StringUtil {

	/**
	 * 私有构造方法，禁止实例化
	 * 
	 */
	private StringUtil() {

	}

	/**
	 * 判断给定的字符序列是否为""或null
	 * 
	 * @param cs 给定的字符序列
	 * @return 当给定的字符序列为""或null时返回true
	 * @author Allen
	 * @date 2017年4月25日下午8:20:48
	 */
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * 判断给定的字符序列是否不为""或null
	 * 
	 * @param cs 给定的字符序列
	 * @return 当给定的字符序列不为""或null时返回true
	 * @author Allen
	 * @date 2017年4月25日下午8:39:24
	 */
	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}

	/**
	 * 校验给定的字符序列是否为""、null或空格
	 * 
	 * @param cs 给定的字符序列
	 * @return 当给定的字符序列为""、null或空格时返回true
	 * @author Allen
	 * @date 2017年4月26日下午7:58:40
	 */
	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 校验给定的字符序列是否不为""、null或空格
	 * 
	 * @param cs 给定的字符序列
	 * @return 当给定的字符序列不为""、null或空格时返回true
	 * @author Allen
	 * @date 2017年4月26日下午7:58:40
	 */
	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	/**
	 * 校验给定的字符序列是否为数值，包括正数、负数、小数
	 * 
	 * @param cs 给定的字符序列
	 * @return 当为数值时返回true
	 * @author Allen
	 * @date 2017年5月3日下午4:14:00
	 */
	public static boolean isNumeric(CharSequence cs) {
		Pattern pattern = Pattern.compile("^-?([0-9]+|[0-9]+\\.[0-9]+)$");
		Matcher isNum = pattern.matcher(cs);
		if (!isNum.matches()) {
			return false;
		}
		return true;

	}

	/**
	 * 校验给定的字符串是否为正整数
	 * 
	 * @param cs 给定的字符序列
	 * @return 当为正整数时返回true
	 */
	public static boolean isPositiveInt(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return false;
		}
		int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据分割符拆分给定的字符串，如果给定的字符串不包含给定的分隔符，则将给定的字符串作为数组的唯一元素返回
	 *
	 * @param str       给定的字符串
	 * @param separator 分隔符
	 * @return 拆分后的数组
	 */
	public static String[] split(final String str, final String separator) {
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return new String[0];
		}
		if (isEmpty(separator) || !str.contains(separator)) {
			return new String[] { str };
		}
		String realSeparator = separator;
		if (".".equals(separator) || "+".equals(separator) || "*".equals(separator) || "|".equals(separator)) {
			realSeparator = "\\" + realSeparator;
		}
		return str.split(realSeparator);
	}

	/**
	 * 将给定字符串首字母改为大写字母
	 *
	 * @param str 给定字符串
	 * @return 首字母大写后的字符串
	 */
	public static String capitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}

		final int firstCodepoint = str.codePointAt(0);
		final int newCodePoint = Character.toTitleCase(firstCodepoint);
		if (firstCodepoint == newCodePoint) {
			return str;
		}

		final int newCodePoints[] = new int[strLen];
		int outOffset = 0;
		newCodePoints[outOffset++] = newCodePoint;
		for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen;) {
			final int codepoint = str.codePointAt(inOffset);
			newCodePoints[outOffset++] = codepoint;
			inOffset += Character.charCount(codepoint);
		}
		return new String(newCodePoints, 0, outOffset);
	}

	/**
	 * 校验给定的字符串是否匹配给定的正则表达式
	 * 
	 * @param regex 正则表达式
	 * @param cs    字符串
	 * @return 匹配结果：true-匹配；false-不匹配
	 */
	public static boolean matcher(String regex, CharSequence cs) {
		Pattern pattern = Pattern.compile(regex);
		Matcher macher = pattern.matcher(cs);
		if (!macher.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 对给定的字符串进行脱敏处理，除了开头要保留的firstReservced位及结尾要保留的lastReserved位，其余都替换为*
	 * 
	 * @param str            要进行脱敏的字符串
	 * @param firstReservced 开头保留的位数，如果小于0，则当0处理
	 * @param lastReserved   结尾保留的位数，如果小于0，则当0处理
	 * @return 脱敏后的字符串
	 */
	public static final String conceal(String str, int firstReservced, int lastReserved) {
		if (isBlank(str)) {
			return null;
		}
		if (firstReservced < 0) {
			firstReservced = 0;
		}
		if (lastReserved < 0) {
			lastReserved = 0;
		}
		int replacedLength = str.length() - (firstReservced + lastReserved);
		if (replacedLength <= 0) {
			// 要保留的位数已经超过了字符串本身的长度，则不需要脱敏，直接返回字符串本身
			return str;
		}
		StringBuilder repaceStr = new StringBuilder();
		for (int i = 0; i < replacedLength; i++) {
			repaceStr.append("*");
		}
		StringBuilder sb = new StringBuilder(str);
		return sb.replace(firstReservced, firstReservced + replacedLength, repaceStr.toString()).toString();
	}

	/**
	 * 隐藏手机号处理，隐藏中间4为为*
	 * 
	 * @param str
	 * @return
	 */
	public static final String concealPhone(String str) {
		if (isBlank(str)) {
			return null;
		}
		if (str.length() < 11) {
			return null;
		}
		int start = str.length() - 8;
		int end = str.length() - 4;
		String pre = str.substring(0, start);
		String sub = str.substring(end, str.length());
		return pre + "****" + sub;
	}

	/**
	 * 去掉字符串结尾的换行符
	 * 
	 * @param str 给定的字符串
	 * @return 去掉结尾换行符的字符串
	 */
	public static final String removeEndingNewlineChar(String str) {
		if (isBlank(str)) {
			return str;
		}
		if (str.endsWith("\r\n")) {
			return str.substring(0, str.length() - 2);
		} else if (str.endsWith("\r")) {
			return str.substring(0, str.length() - 1);
		} else if (str.endsWith("\n")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static void main(String[] args) {
		String s = "8615210341053";
		System.out.println(concealPhone(s));
	}
}
