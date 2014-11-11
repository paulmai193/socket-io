package test;
import socket.server.ServerSocket;
import factory.threadpool.ThreadFactory;

public class RunSimpleServer extends Thread {
	
	public static void main(String[] args) {
		ThreadFactory.getInstance().connect(2);
		ServerSocket server = new ServerSocket(3333);
		server.start();
	}
	
}
