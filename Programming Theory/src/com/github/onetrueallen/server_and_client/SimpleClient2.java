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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SimpleClient2 {
	// GUI
	private JFrame frame;
	private JScrollPane scrollPanel;
	private JPanel userPanel;
	private JTextPane displayText;
	private StyledDocument doc;
	private Style server;
	private Style administrator;
	private Style normalUser;
	private JTextField getText;
	private JButton send;
	private JMenuBar menuBar;
	// Networking
	private Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;
	private String host;
	private int port;

	public static void main(String[] args) {
		new SimpleClient2().go();
	}

	private void connection() {
		do {
			try {
				clientSocket = new Socket(host, port);
				input = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				output = new PrintWriter(clientSocket.getOutputStream());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "CONNECTION FAILED!\n",
						"ERROR", JOptionPane.WARNING_MESSAGE);
				changeServer();
			}
		} while (input == null || output == null);
		String message = "";
		while (!message.equals("\\exit")) {
			try {
				message = input.readLine();
			} catch (IOException e) {
				break;
			}
			if (message != null) {
				try {
					if (message.indexOf("Administrator") == 0) {
						doc.insertString(doc.getLength(),
								String.format("%s%n", message), administrator);
					} else if (message.indexOf(':') != -1) {
						doc.insertString(doc.getLength(),
								String.format("%s%n", message), normalUser);
					} else {
						doc.insertString(doc.getLength(),
								String.format("%s%n", message), server);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else
				message = "";
		}
		try {
			doc.insertString(doc.getLength(),
					String.format("You have disconnected to the server%n"),
					server);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		try {
			input.close();
			output.close();
			clientSocket.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "FAILED TO CLOSE SOCKET!\n",
					"Error", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}
		send.setEnabled(false);
		getText.setEnabled(false);
	}

	private void go() {
		userPanel = new JPanel();
		send = new JButton("Send");
		send.addActionListener(new SendButton());
		getText = new JTextField(30);
		getText.addActionListener(new SendButton());
		userPanel.add(getText);
		userPanel.add(send);
		displayText = new JTextPane();
		displayText.setEditable(false);
		doc = displayText.getStyledDocument();
		server = displayText.addStyle("Server", null);
		StyleConstants
				.setForeground(server, Color.getHSBColor(0.33f, 1f, 0.5f));
		administrator = displayText.addStyle("Administrator", null);
		StyleConstants.setForeground(administrator, Color.RED);
		normalUser = displayText.addStyle("Normal user", null);
		StyleConstants.setForeground(normalUser, Color.BLACK);
		scrollPanel = new JScrollPane(displayText);
		scrollPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Create new menu bar
		menuBar = new JMenuBar();

		// Settings menu
		JMenu settings = new JMenu("Settings");
		JMenuItem changeServer = new JMenuItem("Select Server");
		settings.add(changeServer);
		settings.addSeparator();
		JMenuItem exit = new JMenuItem("Exit Program");
		settings.add(exit);
		changeServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeServer();
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		// Help menu
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,
						"Created by Allen Han - 2015", "About Client",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// Add help and settings menu to the menu bar
		menuBar.add(settings);
		menuBar.add(help);

		// Adds everything to the JFrame
		frame = new JFrame("Client");
		frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, userPanel);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(500, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		changeServer();
		connection();
	}

	private class SendButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String message = getText.getText();
			if (message.length() > 0) {
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

	private void changeServer() {
		host = "127.0.0.1";
		port = 5000;

		// Asks for a custom host and port
		JTextField getHost = new JTextField();
		JTextField getPort = new JTextField();
		getHost.setText(host);
		getPort.setText(Integer.toString(port));
		Object[] message = { "Host:", getHost, "Port:", getPort };

		int option = JOptionPane.showConfirmDialog(null, message,
				"Choose Server", JOptionPane.YES_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			host = getHost.getText();
			try {
				port = Integer.parseInt(getPort.getText());
			} catch (NumberFormatException e) {
				host = "127.0.0.1";
				port = 5000;
				JOptionPane.showMessageDialog(null, String.format(
						"Default Ports will be used!%nHost: %s%nPort: %s",
						host, port), "ERROR INVALID SERVER",
						JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, String.format(
					"Default Ports will be used!%nHost: %s%nPort: %s", host,
					port), "Server Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
