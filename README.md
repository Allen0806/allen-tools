# allen-tools

## 1. 说明
allen-tools为包含基础工具类的工程，所有通用的基础工具类都需要下沉到此工程中。
本工程不需要部署，但需要发布到私服。依赖的环境如下：
- 依赖顶级工程：allen-parent
- 依赖的jdk版本：8

## 2. 目前已定义的工具类

|  name  | remark |
|  ---  | ---  | 
| SpringBeanUtil | 根据Bean的名称获取Spring容器中的bean实例 | 
| DateUtil | 日期型工具类，包含所有日期格式相关的处理方法 | 
| CustomBusinessException | 公共业务异常类 | 
| UidKeyGenerator | UID生成器，百度算法 | 
| JsonUtil | Json工具类，基于Jackson实现 |
| ReflectUtil | 反射工具类 | 
| BaseResult | 接口返回结果封装类，接口返回结果全部使用此类封装 | 
| ResultStatus | 状态码封装类，接口返回的状态码使用此类封装 | 
| DesensitizedUtils | 脱敏工具类 |
| Base64Util | Base64编码工具 |
| StreamUtil | Java的流处理工具类 |
| StringUtil | 字符串工具类，包含所有的字符串处理方法 |
| IDNumberUtil | 身份证号校验工具类 |
| ThreadPoolExecutorUtil | 线程池工具 |
| ValidationGroup | Spring validation 分组标识 |
|  |  |