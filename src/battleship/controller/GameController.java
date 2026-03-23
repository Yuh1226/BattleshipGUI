package battleship.controller;

import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;
import battleship.view.BoardPanel;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameController {
	private Board p1board;
	private Board p2board;
	private BoardPanel p1view;
	private BoardPanel p2view;

	private int p1Hits = 0;
	private int p2Hits = 0;
	private final int Win_Score = 17;
	private boolean isGameOver = false;

	public GameController(Board p1board, Board p2board, BoardPanel p1view, BoardPanel p2view) {
		this.p1board = p1board;
		this.p2board = p2board;
		this.p1view = p1view;
		this.p2view = p2view;

		setUpShip(this.p1board);
		setUpShip(this.p2board);

		showP1Ships();

		setupMouseClicks();
	}

	// Đặt tàu (random cho máy)
	public void setUpShip(Board modelBoard) {
		Random rd = new Random();
		int[] lengths = { 5, 4, 3, 3, 2 };

		for (int len : lengths) {
			boolean placed = false;
			while (!placed) {
				int r = rd.nextInt(9);
				int c = rd.nextInt(9);
				int dir = rd.nextInt(2);

				if (modelBoard.canPlaceShip(len, r, c, dir)) {
					Ship s = new Ship(len);
					s.setLocation(r, c);
					s.setDirection(dir);
					modelBoard.placeShip(s);
					placed = true;
				}
			}
		}
	}

	// Hiện tàu của người chơi
	public void showP1Ships() {
		Node[][] p1Grid = p1board.getGrid();

		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				if (p1Grid[r][c].getVal() == Node.SHIP) {
					p1view.getButton(r, c).setBackground(Color.DARK_GRAY);
					p1view.getButton(r, c).setText("Tàu");
					p1view.getButton(r, c).setForeground(Color.WHITE);
				}
				p1view.getButton(r, c).setEnabled(false);
			}
		}
	}

	// Hành động Click chuột
	private void setupMouseClicks() {
		for (int r = 0; r < Board.size; r++) {
			for (int c = 0; c < Board.size; c++) {
				final int row = r;
				final int col = c;

				p2view.getButton(row, col).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						if (isGameOver)
							return;

						Node targetNode = p2board.getGrid()[row][col];

						// Bỏ qua nếu bắn rồi hoặc bắn trượt
						if (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS) {
							return;
						}

						boolean isHit = p2board.fireAt(row, col);
						p2view.updateButtonState(row, col, isHit);

						if (isHit) {
							p1Hits++;
							checkWinCondition();
						}
						else {
							if (!isGameOver) {
								botFire();
							}
						}
					}
				});
			}
		}
	}

	private void botFire() {
		Random rd = new Random();
		int r, c;
		Node targetNode;
		Node[][] p1Grid = p1board.getGrid();
		boolean isHit;

		do {
			if(isGameOver) break;
			
			do {
				r = rd.nextInt(10);
				c = rd.nextInt(10);
				targetNode = p1Grid[r][c];
			} while (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS);

			isHit = p1board.fireAt(r, c);
			p1view.updateButtonState(r, c, isHit);

			if (isHit) {
				p2Hits++;
				checkWinCondition();
			}
		}
		while(isHit && !isGameOver);
	}

	private void checkWinCondition() {
		if (p1Hits == Win_Score) {
			isGameOver = true;
			JOptionPane.showMessageDialog(null, "BẠN ĐÃ TIÊU DIỆT TOÀN BỘ HÀM ĐỘI ĐỊCH !", "CHIẾN THẮNG",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (p2Hits == Win_Score) {
			isGameOver = true;
			JOptionPane.showMessageDialog(null, "BẠN ĐÃ THUA ,HÀM ĐỘI CỦA BẠN ĐÃ CHÌM HẾT  !", "THẤT BẠI",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
