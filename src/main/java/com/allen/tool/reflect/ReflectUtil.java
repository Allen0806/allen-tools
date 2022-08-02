package com.allen.tool.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.allen.tool.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类
 * 
 * @author allen
 * @since 1.0.0
 */
public class ReflectUtil {

	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

	/**
	 * 调用Getter方法，支持多级，如：对象名.对象名.方法
	 * 
	 * @param obj          给定的对象
	 * @param propertyName 属性名
	 * @return 属性值
	 */
	public static Object invokeGetter(Object obj, String propertyName) {
		Object object = obj;
		for (String name : StringUtil.split(propertyName, ".")) {
			String getterMethodName = GETTER_PREFIX + StringUtil.capitalize(name);
			object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
		}
		return object;
	}

	/**
	 * 调用Setter方法，仅匹配方法名， 支持多级，如：对象名.对象名.方法
	 * 
	 * @param obj          给定的对象
	 * @param propertyName 属性名
	 * @param value        属性值
	 */
	public static void invokeSetter(Object obj, String propertyName, Object value) {
		Object object = obj;
		String[] names = StringUtil.split(propertyName, ".");
		for (int i = 0; i < names.length; i++) {
			if (i < names.length - 1) {
				String getterMethodName = GETTER_PREFIX + StringUtil.capitalize(names[i]);
				object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
			} else {
				String setterMethodName = SETTER_PREFIX + StringUtil.capitalize(names[i]);
				invokeMethodByName(object, setterMethodName, new Object[] { value });
			}
		}
	}

	/**
	 * 直接读取对象属性值，无视private/protected修饰符，不经过getter函数
	 * 
	 * @param obj       给定的对象
	 * @param fieldName 属性名
	 * @return 属性值
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			LOGGER.error("获取[" + fieldName + "]属性值异常", e);
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数
	 * 
	 * @param obj       给定的对象
	 * @param fieldName 属性名
	 * @param value     属性值
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			LOGGER.error("设置[" + fieldName + "]属性值异常", e);
		}
	}

	/**
	 * 直接调用对象方法，无视private/protected修饰符，同时匹配方法名+参数类型。
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用。
	 * 
	 * @param obj            给定的对象
	 * @param methodName     方法名
	 * @param parameterTypes 参数类型
	 * @param args           参数值
	 * @return 方法返回值
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
			final Object[] args) {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 直接调用对象方法，无视private/protected修饰符，只匹配函数名，如果有多个同名函数调用第一个。
	 * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用
	 * 
	 * @param obj        给定的对象
	 * @param methodName 方法名
	 * @param args       参数值
	 * @return 方法返回值
	 */
	public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问。如向上转型到Object仍无法找到, 返回null
	 * 
	 * @param obj       给定的对象
	 * @param fieldName 属性名
	 * @return 对应属性域
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		notNull(obj, "object can't be null");
		notBlank(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
				continue;
			}
		}
		return null;
	}

	/**
	 * 循环向上转型，获取对象的DeclaredMethod，并强制设置为可访问，如向上转型到Object仍无法找到, 返回null，
	 * 匹配函数名+参数类型。用于方法需要被多次调用的情况，先使用本函数先取得Method，然后调用Method.invoke(Object obj,
	 * Object... args)
	 * 
	 * @param obj            对象
	 * @param methodName     方法名
	 * @param parameterTypes 参数类型
	 * @return 反射方法对象
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
			final Class<?>... parameterTypes) {
		notNull(obj, "object can't be null");
		notBlank(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
				continue;
			}
		}
		return null;
	}

	/**
	 * 循环向上转型，获取对象的DeclaredMethod,并强制设置为可访问。 如向上转型到Object仍无法找到，返回null，
	 * 只匹配函数名。用于方法需要被多次调用的情况，先使用本函数先取得Method，然后调用Method.invoke(Object obj, Object...
	 * args)
	 * 
	 * @param obj        对象
	 * @param methodName 方法名
	 * @return 反射方法对象
	 */
	public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
		notNull(obj, "object can't be null");
		notBlank(methodName, "methodName can't be blank");
		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager报错
	 * 
	 * @param method 方法对象
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager报错
	 * 
	 * @param field 域对象
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception
	 * 
	 * @param e 异常
	 * @return 非受检异常
	 */
	private static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}

	/**
	 * 校验给定的对象不能为空，如果为空则抛出空指针异常
	 * 
	 * @param object  给定的待校验的对象
	 * @param message 为空是返回到异常信息
	 * @return 不为空时直接返回校验的对象
	 */
	private static <T> T notNull(final T object, final String message) {
		if (object == null) {
			throw new NullPointerException(message);
		}
		return object;
	}

	/**
	 * 校验给定的字符串不为空，如果为空则抛出异常
	 * 
	 * @param chars   待校验的字符串
	 * @param message 为空时返回的信息
	 * @return 不为空时直接返回校验的对象
	 */
	private static <T extends CharSequence> T notBlank(final T chars, final String message) {
		if (chars == null) {
			throw new NullPointerException(message);
		}
		if (StringUtil.isBlank(chars)) {
			throw new IllegalArgumentException(message);
		}
		return chars;
	}
}
