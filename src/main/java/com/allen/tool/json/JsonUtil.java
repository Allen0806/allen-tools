package com.allen.tool.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Json工具类，基于Jackson实现
 *
 * @author allen
 * @since 1.0
 */
public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * Jackson的ObjectMapper对象
     */
    private static volatile ObjectMapper objectMapper;

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (JsonUtil.class) {
                if (objectMapper != null) {
                    return objectMapper;
                }
                objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            }
        }

        return objectMapper;
    }

    /**
     * 将对象转换为Json字符串
     *
     * @param object 要转换的对象
     * @return 转换后的Json字符串
     * @throws RuntimeException
     */
    public static String object2Json(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("将对象[{}]转为Json字符串时发生异常", object, e);
            throw new RuntimeException("将对象转为Json字符串时发生异常", e);
        }
    }

    /**
     * 将Json字符串转换为指定类型的对象
     *
     * @param json  要转换的Json字符串
     * @param clazz 指定的类型
     * @return 指定类型的对象
     * @throws RuntimeException
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            LOGGER.error("将Json字符串[{}]转换为[{}]类型对象时发生异常", json, clazz.getName(), e);
            throw new RuntimeException("将Json字符串转换为对象时发生异常", e);
        }
    }

    /**
     * 将Json字符串转换为自定义类型或复杂类型的对象
     *
     * @param json         要转换的Json字符串
     * @param valueTypeRef 自定义类型或复杂类型
     * @return 给定类型的对象
     * @throws RuntimeException
     */
    public static <T> T json2Object(String json, TypeReference<T> valueTypeRef) {
        try {
            return getObjectMapper().readValue(json, valueTypeRef);
        } catch (Exception e) {
            LOGGER.error("将Json字符串[{}]转换为[{}]类型对象时发生异常", json, valueTypeRef.getType().getTypeName(), e);
            throw new RuntimeException("将Json字符串转换为对象时发生异常", e);
        }
    }

    /**
     * 将Json字符串转换为Map类型的对象
     *
     * @param json 要转换的Json字符串
     * @return Map对象，key为String类型，value为Object类型
     * @throws RuntimeException
     */
    public static Map<String, Object> json2Map(String json) {
        return json2Object(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 将对象转换为json，给定的属性不输出
     *
     * @param object      给定对象
     * @param ignoreField 需要忽略的属性
     * @return json字符串
     */
    public static String object2Json(Object object, String ignoreField) {
        List<String> ignoreFields = new ArrayList<>();
        ignoreFields.add(ignoreField);
        return object2Json(object, ignoreFields);
    }

    /**
     * 将对象转换为json，给定的属性不输出
     *
     * @param object       给定对象
     * @param ignoreFields 需要忽略的属性列表
     * @return json字符串
     */
    public static String object2Json(Object object, List<String> ignoreFields) {
        Object tmp = json2Object(object2Json(object), new TypeReference<Object>() {
        });
        remove(tmp, ignoreFields);
        return object2Json(tmp);
    }

    /**
     * 递归移除给定的属性
     *
     * @param object
     * @param fields
     */
    private static void remove(Object object, List<String> fields) {
        if (object == null) {
            return;
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (collection.isEmpty()) {
                return;
            }
            for (Object item : (Collection) object) {
                remove(item, fields);
            }
        } else if (object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            map = removeFromMap(map, fields);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                remove(value, fields);
            }
        }
    }

    /**
     * 从map对象中移除给定的key
     *
     * @param map  map
     * @param keys key集合
     * @return 移除给定key后的map
     */
    private static Map<String, Object> removeFromMap(Map<String, Object> map, List<String> keys) {
        if (map == null || map.isEmpty()) {
            return map;
        }
        if (keys == null || keys.isEmpty()) {
            return map;
        }
        for (String key : keys) {
            map.remove(key);
        }
        return map;
    }
}
