package com.github.onetrueallen.simple_ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RectangleMovement implements Runnable {
	JFrame frame;
	MyPanel panel;
	private int x = 100;
	private int y = 100;
	private int playerX = 400;
	private int playerY = 350;
	private int horizontalGoal = 0;
	private int verticalGoal = 0;

	public static void main(String[] args) {

		new RectangleMovement().go();

	}

	private void go() {
		frame = new JFrame("Worst Game Ever");
		panel = new MyPanel();
		MyPanel buttonPanel = new MyPanel();
		JButton left = new JButton("Left");
		JButton right = new JButton("Right");
		JButton down = new JButton("Down");
		JButton up = new JButton("Up");
		left.addActionListener(new LeftButton());
		right.addActionListener(new RightButton());
		down.addActionListener(new DownButton());
		up.addActionListener(new UpButton());
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(left);
		buttonPanel.add(down);
		buttonPanel.add(up);
		buttonPanel.add(right);

		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		Thread player = new Thread(this);
		player.start();
		animate();
	}

	private class MyPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(x, y, 10, 10);
			g.setColor(Color.BLUE);
			g.fillRect(playerX, playerY, 10, 10);
		}
	}

	class LeftButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			horizontalGoal = -5;
		}
	}

	class RightButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			horizontalGoal = 5;
		}
	}

	class UpButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			verticalGoal = -5;
		}
	}

	class DownButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			verticalGoal = 5;
		}
	}

	/*
	 * Animates the player
	 */
	public void run() {
		while (true) {
			if (horizontalGoal < 0 && playerX > 0) {
				playerX--;
				horizontalGoal++;
			} else if (horizontalGoal > 0 && playerX + 20 < frame.getWidth()) {
				playerX++;
				horizontalGoal--;
			}
			if (verticalGoal < 0 && playerY > 0) {
				playerY--;
				verticalGoal++;
			} else if (verticalGoal > 0 && playerY + 10 < 400) {
				playerY++;
				verticalGoal--;
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (intersects()){
				playerX = 400;
				playerY = 350;
			}
			frame.repaint();
		}
	}

	private void animate() {
		boolean direction = true;
		while (true) {
			if (y <= 100) {
				direction = true;
			} else if (y >= 400) {
				direction = false;
			}
			if (direction) {
				y++;
			} else {
				y--;
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			frame.repaint();
		}
	}

	private boolean intersects() {
		if (x <= playerX && playerX <= x + 10 && y <= playerY
				&& playerY <= y + 10) {
			return true;
		}
		return false;
	}
}
