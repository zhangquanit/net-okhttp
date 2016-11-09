package com.example.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class ProxyClientDemo {

	/**
	 * 通过系统属性设置代理
	 * <p>
	 * 这种方式对所有的http请求生效
	 * </p>
	 * 
	 * @param proxyAddr
	 * @param proxyPort
	 */
	private static void setProxy(String proxyAddr, int proxyPort) {
		System.setProperty("java.net.useSystemProxies", "true");
		// HTTP代理
		System.setProperty("http.proxyHost", proxyAddr);
		System.setProperty("http.proxyPort", proxyPort + "");
		// HTTPS代理
		System.setProperty("https.proxyHost", proxyAddr);
		System.setProperty("https.proxyPort", proxyPort + "");
	}

	/**
	 * 如果代理服务器要验证用户
	 * 
	 * @param username
	 * @param password
	 */
	private static void initAuthenticator(final String username,
			final String password) {
		if (null == username || "".equals(username) || null == password
				|| "".equals(password)) {
			return;
		}
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,
						password.toCharArray());
			}
		};
		Authenticator.setDefault(authenticator);
	}

	/**
	 * @param uri
	 * @return
	 */
	private static Proxy getSystemProxy(String uri) {
		Proxy proxy = null;
		try {
			ProxySelector ps = ProxySelector.getDefault();
			List<Proxy> proxyList = ps.select(new URI(uri));
			for (Proxy p : proxyList) {
				InetSocketAddress addr = (InetSocketAddress) p.address();
				if (null != addr) {
					proxy = p;
					// System.out.println("代理类型 : " + p.type());
					// System.out.println("代理主机 : " + addr.getHostName());
					// System.out.println("代理端口 : " + addr.getPort());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proxy;
	}

	/**
	 * 测试调用
	 */
	private static void invoke() throws Exception {
		String urlStr = "http://www.baidu.com/";

		InetAddress localHost = Inet4Address.getLocalHost();
		String proxyHost = localHost.getHostName();
		int proxyPort = ProxyServer.port;

		Proxy proxy = null;
		// 设置代理方式一：通过系统属性设置代理 这种方式对所有本类型请求都起作用
		// setProxy(proxyHost, HttpProxySocket.port);
		// proxy = getSystemProxy(urlStr);// 获取代理
	

		// 设置代理方式二：通过java.net.Proxy类，本方式只对当前请求起作用
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,
				proxyPort));

		System.out.println("请求代理: " + proxy);

		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = null;
			if (proxy != null) {
				conn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			// 如果代理服务器需要验证用户
			
			
			/**
			 * 通过设置Http消息头，连接到proxy的身份验证信息 格式：Proxy-Authorization: Basic
			 * Base64.encode(user:password)
			 */
			//代理认证方式一
			// initAuthenticator(null, null);
			//代理认证方式二
			
			// String headerKey = "Proxy-Authorization";
			// String headerValue ="Basic " + Base64.encode(user+":"+password);
			// conn.setRequestProperty(headerKey, headerValue);
			
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write("客户端请求消息\r\n".getBytes("utf-8"));
			outputStream.flush();

			InputStream is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is,"utf-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		invoke();
	}

	private static void printSysProperties() {
		Properties properties = System.getProperties();
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			System.out.println(key + "==" + System.getProperty(key));
		}

	}

}