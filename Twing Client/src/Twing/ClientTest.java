package Twing;

import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		Client cln = new Client("127.0.0.1");
		cln.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cln.startRunning();

	}

}
