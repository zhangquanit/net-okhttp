package com.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	private Socket socket;

	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.connect();
		client.start();
	}

	public Client() throws IOException {
		socket = new Socket();
	}

	public void connect() throws IOException {
		try {
			InetSocketAddress address = new InetSocketAddress(
					Inet4Address.getLocalHost(), 2234);
			System.out.println("client---------- start connect");
			socket.connect(address, 5 * 1000);
			System.out.println("client----------end connect");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void start() {
		System.out.println("client----------start");
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream);
			printWriter.println("client发送的消息");
			printWriter.flush();
		
			inputStream = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream, "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.shutdownInput();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.shutdownOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}