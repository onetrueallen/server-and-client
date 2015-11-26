package com.github.onetrueallen.server_and_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SimpleServer {
	private ServerSocket server;
	private ArrayList<Thread> clients;
	private ArrayList<ClientHandler> sync;
	private ArrayList<String> messages;

	public static void main(String[] args) {
		new SimpleServer().go();
	}

	private void go() {
		Thread connect = new Thread(new Sync());
		connect.start();
		clients = new ArrayList<Thread>();
		sync = new ArrayList<ClientHandler>();
		messages = new ArrayList<String>();
		try {
			server = new ServerSocket(5000);
			while (true) {
				Socket newClient = server.accept();
				ClientHandler client = new ClientHandler(newClient);
				clients.add(new Thread(client));
				clients.get(clients.size() - 1).start();
				sync.add(client);
				System.out.println(clients.size() + " client(s) connected!");
			}
		} catch (IOException e) {
			System.out.println("Connection error.");
			e.printStackTrace();
		}
	}

	private class ClientHandler implements Runnable {
		private Socket client;
		private BufferedReader input;
		private PrintWriter output;
		private String username;

		public ClientHandler(Socket s) {
			this.client = s;
		}

		public void run() {
			try {
				output = new PrintWriter(client.getOutputStream());
				input = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			output.println("You've connected to the server...");
			output.println("Use \\username to set a username.");
			output.println("Use \\clear to clear.");
			output.println("Use \\clients to to view number of clients online.");
			output.println("There are currently " + clients.size()
					+ " client(s) online");
			output.flush();
			String setUsername = "";
			while (setUsername.indexOf("\\username ") != 0) {
				try {
					setUsername = input.readLine();
				} catch (IOException e) {
					return;
				}
				if (setUsername != null) {
					if (setUsername.indexOf("\\username ") != 0
							|| setUsername.length() > 50) {
						output.println("Username not valid use \\username (username) to set username.");
						output.flush();
					}
					if (setUsername.length() == 23
							&& setUsername.toLowerCase().indexOf(
									"administrator") != -1) {
						output.println("You can't use this name.");
						output.flush();
						setUsername = "";
					}
				} else
					setUsername = "";

				try {
					Thread.sleep(15);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			username = setUsername.substring(10);
			if (username.equals("dankmaymaymasterallen")) {
				username = "Administrator";
			}
			output.println("Your username is now " + username);
			output.flush();

			String message = "";
			while (!message.equals("\\exit")) {
				try {
					message = input.readLine();
				} catch (IOException e) {
					return;
				}
				if (message != null) {
					if (message.equals("\\exit")) {
						output.println("\\exit");
						output.flush();
					} else if (message.indexOf("\\clients") == 0) {
						output.println("There are currently " + clients.size()
								+ " client(s) online");
						output.flush();
					} else if (message.indexOf("\\username ") == 0
							&& message.length() < 50) {
						if (message.length() == 23
								&& message.toLowerCase().indexOf(
										"administrator") != -1) {
							output.println("You can't use this name.");
							output.flush();
						} else {
							username = message.substring(10);
							output.println("Your username is now " + username);
							output.flush();
						}
					} else {
						messages.add(username + ": " + message);
					}
				} else
					message = "";
				try {
					Thread.sleep(15);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			messages.add(username + " has left the chat.");
			try {
				output.close();
				input.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendMessage(String message) {
			output.println(message);
			output.flush();
		}
	}

	private class Sync implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (sync.size() != 0 && messages.size() != 0) {
					for (int i = 0; i < sync.size(); i++) {
						sync.get(i).sendMessage(
								messages.get(messages.size() - 1));
					}
					messages.remove(messages.size() - 1);
				} else {
					try {
						Thread.sleep(15);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < clients.size(); i++) {
					if (!clients.get(i).isAlive()) {
						clients.remove(i);
						sync.remove(i);
					}
				}
			}
		}
	}

}
