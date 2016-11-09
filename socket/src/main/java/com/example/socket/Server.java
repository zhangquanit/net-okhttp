package com.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private boolean cmd_start;
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server() throws IOException {
		serverSocket = new ServerSocket(2234);
	}

	public void start() {
		// try {
		// System.out.println("server-------开始休眠");
		// Thread.sleep(20*1000l);
		// System.out.println("server-------结束休眠");
		//
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		cmd_start = true;
		while (cmd_start) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println(socket + "请求连接");
				handleRequest(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		cmd_start = false;
		if (null != serverSocket) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void handleRequest(final Socket socket) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					socket.setSoTimeout(5 * 1000);
					inputStream = socket.getInputStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(inputStream, "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						// 给客户端发送数据
						OutputStream out = socket.getOutputStream();
						out.write("从服务端返回的数据\r\n".getBytes());
						out.flush();
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// try {
					// socket.shutdownInput();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
					// try {
					// socket.shutdownOutput();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
					// try {
					// socket.close();
					// serverSocket.close();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				}
			}
		}).start();
	}
}