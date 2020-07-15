package Twing;

import javax.swing.JFrame;


public class ServerTest {

	public static void main(String[] args) {
		Server svr = new Server();
		svr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		svr.startRunning();
	}

}
