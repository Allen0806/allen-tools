package com.allen.tool.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen.tool.string.StringUtil;

/**
 * Apache的HttpClient工具类，用于处理http请求及相应等操作
 * 
 * @author Allen
 * @since 1.0.0
 *
 */
public final class HttpClientUtil {

	/**
	 * 日志工具
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * 默认字符集
	 */
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * httpClient单例对象
	 */
	private static volatile CloseableHttpClient httpClient;

	/**
	 * 获取Apache的HttpClient实例
	 * 
	 * @return HttpClient实例
	 */
	public static CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			synchronized (HttpClientUtil.class) {
				// 加此判断防止重复初始化
				if (httpClient == null) {
					init();
				}
			}
		}
		return httpClient;
	}

	/**
	 * 执行get方法
	 * 
	 * @param url 请求的url地址
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doGet(String url) throws Exception {
		return doGet(url, -1);
	}

	/**
	 * 执行get方法
	 * 
	 * @param url           请求的url地址
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doGet(String url, int socketTimeout) throws Exception {
		String result = null;
		if (StringUtil.isBlank(url)) {
			return result;
		}
		HttpGet httpGet = new HttpGet(url);
		return doGet(httpGet, socketTimeout);
	}

	/**
	 * 执行get方法
	 * 
	 * @param httpGet       HttpGet对象
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doGet(HttpGet httpGet, int socketTimeout) throws Exception {
		String result = null;
		if (httpGet == null) {
			return result;
		}
		if (socketTimeout != -1 && (socketTimeout < 1 || socketTimeout > 60)) {
			return result;
		}
		HttpResponse response = null;
		try {
			if (socketTimeout != -1) {
				// 请求配置，设置给定的响应时间socketTimeout
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout * 1000)
						.setConnectTimeout(5 * 1000).setConnectionRequestTimeout(5 * 1000).build();
				httpGet.setConfig(requestConfig);
			}
			response = getHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				EntityUtils.consume(response.getEntity());
			} else {
				result = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			if (response != null) {
				EntityUtils.consume(response.getEntity());
			}
			LOGGER.error("请求url发生错误，发生错误的url为：{}", httpGet.getURI(), e);
			throw e;
		}
		return result;
	}

	/**
	 * 执行form的post请求
	 * 
	 * @param url           请求url，不能为空
	 * @param paramMap      参数map，不能为空
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doPost(String url, Map<String, String> paramMap, int socketTimeout) throws Exception {
		String result = null;
		if (StringUtil.isBlank(url)) {
			return result;
		}

		if (socketTimeout != -1 && (socketTimeout < 1 || socketTimeout > 60)) {
			return result;
		}

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (paramMap != null && paramMap.size() > 0) {
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				nameValuePairs.add(new BasicNameValuePair(name, value));
			}
		}

		HttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			if (socketTimeout != -1) {
				// 请求配置，设置给定的响应时间socketTimeout
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout * 1000)
						.setConnectTimeout(5 * 1000).setConnectionRequestTimeout(5 * 1000).build();
				httpPost.setConfig(requestConfig);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
			response = getHttpClient().execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				EntityUtils.consume(response.getEntity());
			} else {
				result = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			if (response != null) {
				EntityUtils.consume(response.getEntity());
			}
			LOGGER.error("请求url发生错误，发生错误的url为：{}", url, e);
			throw e;
		}

		return result;
	}

	/**
	 * 执行参数为xml的post请求
	 * 
	 * @param url           请求url，不能为空
	 * @param xmlString     xml字符串，不能为空
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doPost4Xml(String url, String xmlString, int socketTimeout) throws Exception {
		return doPost(url, "text/xml", xmlString, socketTimeout);
	}

	/**
	 * 执行参数为Json的post请求
	 * 
	 * @param url           请求url，不能为空
	 * @param json          Json字符串，不能为空
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doPost4Json(String url, String json, int socketTimeout) throws Exception {
		return doPost(url, "application/json", json, socketTimeout);
	}

	/**
	 * 执行给定参数的post请求
	 * 
	 * @param url           请求url，不能为空
	 * @param contentType   请求体的多媒体类型
	 * @param bodyString    请求体字符串（XML或Json），不能为空
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws IOException 如果出错会抛出IO异常
	 */
	public static String doPost(String url, String contentType, String bodyString, int socketTimeout) throws Exception {
		String result = null;
		if (StringUtil.isBlank(url)) {
			return result;
		}
		HttpPost httpPost = new HttpPost(url);
		return doPost(httpPost, contentType, bodyString, socketTimeout);
	}

	/**
	 * 执行给定参数的post请求
	 * 
	 * @param httpPost      HttpPost对象
	 * @param contentType   请求体的多媒体类型
	 * @param bodyString    请求体字符串（XML或Json），不能为空
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doPost(HttpPost httpPost, String contentType, String bodyString, int socketTimeout)
			throws Exception {
		String result = null;
		if (httpPost == null) {
			return result;
		}
		if (StringUtil.isBlank(bodyString)) {
			return result;
		}
		if (StringUtil.isBlank(contentType)) {
			return result;
		}
		httpPost.setEntity(new StringEntity(bodyString, ContentType.create(contentType, DEFAULT_CHARSET)));
		return doPost(httpPost, socketTimeout);
	}

	/**
	 * 执行给定参数的post请求
	 * 
	 * @param httpPost      HttpPost对象
	 * @param socketTimeout 响应请求的失效时间，单位为秒，取值范围为[1, 60]，当为-1时表示采用默认值，如果为其他值则直接返回null
	 * @return 请求结果
	 * @throws Exception 如果出错会抛出异常
	 */
	public static String doPost(HttpPost httpPost, int socketTimeout) throws Exception {
		String result = null;
		if (httpPost == null) {
			return result;
		}
		if (socketTimeout != -1 && (socketTimeout < 1 || socketTimeout > 60)) {
			return result;
		}
		HttpResponse response = null;
		try {
			if (socketTimeout != -1) {
				// 请求配置，设置给定的响应时间socketTimeout
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout * 1000)
						.setConnectTimeout(5 * 1000).setConnectionRequestTimeout(5 * 1000).build();
				httpPost.setConfig(requestConfig);
			}
			response = getHttpClient().execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				EntityUtils.consume(response.getEntity());
			} else {
				result = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			if (response != null) {
				EntityUtils.consume(response.getEntity());
			}
			LOGGER.error("请求url发生错误，发生错误的url为：{}，请求体为：{}", httpPost.getURI(), httpPost.getEntity(), e);
			throw e;
		}

		return result;
	}
	
	/**
	 * 禁止实例化
	 */
	private HttpClientUtil() {

	}

	/**
	 * 初始化HttpClient对象
	 */
	private static void init() {

		// 注册访问协议相关的socket工厂
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();

		// HttpConnection工厂：配置写请求/解析响应处理器
		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory = new ManagedHttpClientConnectionFactory(
				DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);

		// DNS解析器
		DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;

		// 默认为Socket配置
		SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

		// 定义连接池管理
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry, connectionFactory, dnsResolver);
		// 设置整个连接池最大连接数
		connectionManager.setMaxTotal(300);
		// 设置每个路由的默认最大连接数
		connectionManager.setDefaultMaxPerRoute(200);
		// 设置默认socket配置
		connectionManager.setDefaultSocketConfig(defaultSocketConfig);
		// 在从连接池获取连接时，连接不活跃多长时间后进行一次验证，默认为2s
		connectionManager.setValidateAfterInactivity(5 * 1000);

		// 请求配置
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5 * 1000).setConnectTimeout(5 * 1000)
				.setConnectionRequestTimeout(5 * 1000).build();

		httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				// 连接池不共享模式
				.setConnectionManagerShared(false)
				// 定期回收空闲连接
				.evictIdleConnections(60, TimeUnit.SECONDS)
				// 定期回收过期连接
				.evictExpiredConnections()
				// 连接存活时间，如果不设置，则根据长连接信息决定
				.setConnectionTimeToLive(60, TimeUnit.SECONDS)
				// 设置默认请求配置
				.setDefaultRequestConfig(requestConfig)
				// 连接重用策略，即是否能keepAlive
				.setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
				// 长连接配置，最大空闲时间
				.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
				// 设置重试次数，默认3次，此处禁用，需要时开启
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();

		// JVM停止或重启时，关闭连接池释放掉连接
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					httpClient.close();
				} catch (IOException e) {
					LOGGER.error("关闭httpClient连接错误", e);
				}
			}
		});
	}

}
