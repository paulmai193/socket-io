import io.Reader;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

import socket.ClientConnectSocket;
import socket.ServerSocket;

import datapackage.PingData;
import define.Command;
import factory.ThreadFactory;

public class Run {
	
	public static void main(String[] args) throws InterruptedException {
		// Start thread factory
		ThreadFactory.getInstance().connect(5);
		
		// Start server with thread 1
		final ServerSocket server = new ServerSocket(3333);
		ThreadFactory.getInstance().start(new Runnable() {
			
			@Override
			public void run() {
				server.start();
			}
		});
		
		// Start client with thread 2
		testsocket();
		
		for (ClientConnectSocket client : server._hashSet) {
			try {
				PingData data = new PingData();
				data.setNumber(9999);
				client.getWriter().applyStream(client.getOutputStream(), data, Command.PING);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// stop server
		server.Stop();
	}
	
	static void testsocket() throws InterruptedException {
		String serverName = "localhost";
		int port = 3333;
		try {
			System.out.println("Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(serverName, port);
			//			DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
			
			//			outputStream.writeInt(Command.PING);
			//			outputStream.writeInt(1988);
			//			outputStream.flush();
			
			Reader reader = new Reader();
			int number = reader.readInt(ByteBuffer.wrap(IOUtils.toByteArray(client.getInputStream())));
			System.out.println("Receive ping number " + number + " from server");
			
			//			client.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
