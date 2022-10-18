package com.allen.tool.date;

import com.allen.tool.string.StringUtil;
import com.allen.tool.thread.ThreadPoolExecutorUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 日期型工具类，包含所有日期格式相关的处理方法
 *
 * @author luoxuetong
 * @since 1.0.0
 */
public final class DateUtil {

    /**
     * 日期格式化器：yyyy-MM-dd
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 日期时间格式化器：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间格式化器：HH:mm:ss
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 存放不同的日期模板格式的SimpleDateFormat对应的ThreadLocal的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> simpleDateFormatMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 将给定格式的日期字符串转换为Date对象
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式字符串
     * @return 转换后的Date对象
     * @throws IllegalArgumentException 如果日期字符串或日期格式字符串为空 ，或者日期格式字符串不合法
     * @throws ParseException           如果不能解析日期字符串
     * @author luoxuetong
     * @date 2018年5月18日 下午8:08:30
     */
    public static Date toDate(String dateStr, String pattern) throws ParseException {
        if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(pattern)) {
            throw new IllegalArgumentException("日期字符串及日期格式字符串不能为空");
        }
        return getSimpleDateFormat(pattern).parse(dateStr);
    }

    /**
     * 将给定的日期对象转换为指定格式的日期字符串
     *
     * @param date    日期对象
     * @param pattern 日期格式字符串
     * @return 格式化后的日期字符串
     * @throws IllegalArgumentException 如果日期对象或日期格式字符串为空 ，或者日期格式字符串不合法
     * @author luoxuetong
     * @date 2018年5月18日 下午8:08:30
     */
    public static String toString(Date date, String pattern) {
        if (date == null || StringUtil.isEmpty(pattern)) {
            throw new IllegalArgumentException("日期对象及日期格式字符串不能为空");
        }
        return getSimpleDateFormat(pattern).format(date);
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
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return true-满足；false-不满足
     * @author luoxuetong
     * @since 1.0
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = getSimpleDateFormat(pattern);
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

    public static void main(String[] args) {
        String format2 = "yyyy-MM-dd HH:mm:ss";
        for (int i = 0; i < 3; i++) {
            String dateStr = "2020-08-08 20:12:00";
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Date date = toDate(dateStr, format2);
                        System.out.println("test2-" + date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            ThreadPoolExecutor executor = ThreadPoolExecutorUtil.getExecutor("dateFormatterTest");
            executor.execute(r);
        }
    }

    /**
     * 禁止实例化
     */
    private DateUtil() {

    }

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return SimpleDateFormat对象
     */
    private static SimpleDateFormat getSimpleDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> threadLocal = simpleDateFormatMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (threadLocal == null) {
            synchronized (DateUtil.class) {
                threadLocal = simpleDateFormatMap.get(pattern);
                if (threadLocal == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    threadLocal = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    simpleDateFormatMap.put(pattern, threadLocal);
                }
            }
        }

        return threadLocal.get();
    }
}
