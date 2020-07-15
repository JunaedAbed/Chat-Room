package Twing;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output; //goes away from me
	private ObjectInputStream input; //comes to me
	private String message = "";
	private String serverIP;
	private Socket connection; //setup the connection between two pcs
	
	
	public Client(String host) {
		super("Twing Client"); 
		serverIP = host;
		
		userText = new JTextField();
		userText.setEditable(false); //by default before any connection, not allowed to type in the msg box
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					//after hitting enter
					sendData(event.getActionCommand()); //gets the string msg
					userText.setText(""); //text box is empty after sending the message
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(500, 350);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eof) {
			showMessage("\nClient terminated connection");
		}catch(IOException iox) {
			iox.printStackTrace();
		}finally {
			closeCrap();
		}
	}

	//connects to server
	private void connectToServer() throws IOException{
		showMessage("Bridging connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789); //(ip add, port)
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream()); //creating the pathway that connects to another pc to send
		output.flush(); //sends the leftover bytes
		input = new ObjectInputStream(connection.getInputStream()); //setup the pathway to receive
		showMessage("\nStreams are now setup! \n");	
	}
	
	//during chat
	private void whileChatting() throws IOException{
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException e) {
				showMessage("\nLost in the bytes(-.-)");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//close streams and sockets after done chatting 
	private void closeCrap() {
		showMessage("\n\nClosing connnection... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	//sends a msg to server
	private void sendData(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException e) {
			chatWindow.append("\nERROR");
		}
	}
	
	//updates chat window
	public void showMessage(final String text) {
		//thread that updates the part of the GUI
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(text);
					}
				}
		);
	}
	
	//let the user type
	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}
		);
	}
}
