package Twing;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output; //goes away from me
	private ObjectInputStream input; //comes to me
	private ServerSocket server; 
	private Socket connection; //setup the connection between two pcs
	
	public Server() {
		super("Twing");
		userText = new JTextField();
		userText.setEditable(false); //by default before any connection, not allowed to type in the msg box
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					//after hitting enter
					sendMessage(event.getActionCommand()); //gets the string msg
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
	
	//set up and run server
	public void startRunning() {
		try {
			
			server = new ServerSocket(6789, 100);//(port, backlog)
			while(true) {
				try {
					//connect and have conversation
					waitForConnection(); 
					setupStreams();  
					whileChatting(); 
					
				}catch(EOFException eofex) {
					showMessage("\n\nServer ended the connection!");
				}finally {
					closeCrap();
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	

	//wait for someone to connect then display connection info
	private void waitForConnection() throws IOException{
		showMessage("waiting... \n");
		connection = server.accept(); //blocks until a connection is made
		showMessage("Now Connected to: " + connection.getInetAddress().getHostName());
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
		String msg = "You are now connected\n";
		showMessage(msg);
		ableToType(true); //allows typing
		do {
			//have a conversation
			try {
				msg = (String) input.readObject(); //read the message
				showMessage("\n" + msg);
			}catch(ClassNotFoundException cls) {
				showMessage("\nThat user is retarted");
			}
		}while(!msg.equals("CLIENT - END"));
	}
	
	//close streams and sockets after done chatting 
	private void closeCrap() {
		showMessage("\nClosing connnection... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	//sends a msg to client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message); //to the output stream
			output.flush();
			showMessage("\nSERVER - " + message); //displays in the GUI
		}catch(IOException e) {
			chatWindow.append("\n ERROR");
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
