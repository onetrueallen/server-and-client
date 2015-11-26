package com.github.onetrueallen.server_and_client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SimpleClient {
	// GUI
	JFrame frame;
	JScrollPane scrollPanel;
	JPanel userPanel;
	JTextArea displayText;
	JTextField getText;
	JButton send;
	String username;

	// Networking
	Socket clientSocket;
	BufferedReader input;
	PrintWriter output;

	public static void main(String[] args) {
		new SimpleClient().go();
	}

	private void connection() {
		try {
			clientSocket = new Socket("127.0.0.1", 5000);
			input = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "CONNECTION FAILED!\n");
			System.exit(-1);
		}
		String message = "";
		while (!message.equals("\\exit")) {
			try {
				message = input.readLine();
			} catch (IOException e) {
				break;
			}
			if (message != null) {
				displayText.append(message + "\n");
			} else
				message = "";
		}
		displayText.append("You have disconnected to the server\n");
		try {
			input.close();
			output.close();
			clientSocket.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "FAILED TO CLOSE SOCKET!\n");
			System.exit(-1);
		}
		send.setEnabled(false);
		getText.setEnabled(false);
	}

	private void go() {
		frame = new JFrame("Client");
		userPanel = new JPanel();
		send = new JButton("Send");
		send.addActionListener(new SendButton());
		getText = new JTextField(30);
		getText.addActionListener(new SendButton());
		userPanel.add(getText);
		userPanel.add(send);
		displayText = new JTextArea();
		displayText.setEditable(false);
		displayText.setLineWrap(true);
		displayText.setWrapStyleWord(true);
		scrollPanel = new JScrollPane(displayText);
		scrollPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, userPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(500, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		connection();
	}

	private class SendButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String message = getText.getText();
			if (message.indexOf("\\username ") == 0) {
				if (message.length() > 10 && message.length() < 50) {
					username = message.substring(10);
					displayText
							.append("Username has successfully been changed to: "
									+ username + "\n");
					output.println(message);
					output.flush();
				} else {
					displayText.append("INVALID USERNAME!" + "\n");
				}
				getText.setText("");
			} else if (message.length() > 0) {
				getText.setText("");
				if (message.equals("\\clear")) {
					displayText.setText("");
				} else {
					output.println(message);
					output.flush();
				}
			}
		}

	}
}
