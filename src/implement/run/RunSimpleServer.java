package implement.run;

import implement.server.SocketServerImpl;

public class RunSimpleServer extends Thread {

	public static void main(String[] args) {

		SocketServerImpl server = new SocketServerImpl(3333, 5000, 360000);
		server.start();

	}

}
