package com.example.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 代理服务器
 * 
 * @author 张全
 */
public class ProxyServer {
	public static final int port = 9988;
	static final String CTRLF = "\r\n";

	public static void main(String[] args) {
		startServer();
	}

	private static void startServer() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new Client(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (null != serverSocket) {
				try {
					serverSocket.close();
					System.out.println("服务关闭");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
	}

	private static class Client implements Runnable {
		private Socket socket;

		public Client(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				System.out.println(socket.getInetAddress() + "请求连接");
				InputStream inputStream = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream, "utf-8"));
				System.out.println("-------请求消息---------");
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);

					if (line.isEmpty()) {
						String text = "代理服务器收到消息了，感谢访问";
						// 消息头
						StringBuffer responseData = new StringBuffer();
						responseData.append("HTTP/1.1 200 OK").append(CTRLF);
						responseData.append("Server: WWW Server/1.1").append(
								CTRLF);
						responseData.append(
								"Content-Type: text/html; charset=utf-8")
								.append(CTRLF);
						responseData.append("Connection: close").append(CTRLF);
						responseData.append("Content-Length: " + text.length())
								.append(CTRLF);
						responseData.append(CTRLF);
						// 实体数据
						responseData.append(text + CTRLF);

						// 返回数据
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(responseData.toString().getBytes(
								"utf-8"));
						outputStream.flush();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
