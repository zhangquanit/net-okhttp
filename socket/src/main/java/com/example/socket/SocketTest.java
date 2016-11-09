package com.example.socket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

/**
 * @author 张全
 */
public class SocketTest {

	public static void main(String[] args) throws Exception {
		String path = null;
		// path="http://www.umeng.com/?ticket=ST-1447037668rqY-CNsOgmsIFCiGpaz";
		path = "http://www.baidu.com";

		// testInet4Address(path);
		// testHttpUrlConn(path);
//		 testSocket(path);
		 printSokcetGets(path);

	}

	private static void testInet4Address(String path) throws Exception {
		InetAddress address = Inet4Address.getByName(path);
		System.out.println(address.getHostName());
		System.out.println(address.getHostAddress());
		System.out.println(address.getCanonicalHostName());
	}

	private static void testHttpUrlConn(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "utf-8"));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
		conn.disconnect();
	}

	private static void testSocket(String path) throws Exception {
		URI uri = new URI(path);
		int port = uri.getPort();
		port = port == -1 ? 80 : port;
		InetSocketAddress address = new InetSocketAddress(uri.getHost(), port);

		Socket socket = new Socket();
		socket.connect(address, 5 * 1000);
		System.out.println(socket);
		OutputStream outputStream = socket.getOutputStream();

		StringBuffer request = new StringBuffer();
		// 请求行
		request.append("GET " + path + " HTTP/1.1").append("\r\n");
		// 请求消息头
		request.append("Host: " + uri.getHost()).append("\r\n");
		request.append("\r\n");// 这里一定要一个回车换行，表示消息头完，不然服务器会等待

		outputStream.write(request.toString().getBytes());
		outputStream.flush();

		InputStream inputStream = socket.getInputStream();

		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, "utf-8"));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
		socket.close();
	}
	
	private static void printSokcetGets(String path)throws Exception{
		URI uri = new URI(path);
		int port = uri.getPort();
		port = port == -1 ? 80 : port;
		Socket socket = new Socket(uri.getHost(), port);
		Method[] methods = socket.getClass().getDeclaredMethods();
		for(Method method:methods){
			String name = method.getName();
			if(name.startsWith("get")){
				method.setAccessible(true);
				Object value = method.invoke(socket, null);
				System.out.println(name+"=="+value);
			}
		}
	}
}
