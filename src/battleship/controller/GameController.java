package battleship.controller;

import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;
import battleship.view.BoardPanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameController {
	private Board p1board;
	private Board p2board;
	private BoardPanel p1view;
	private BoardPanel p2view;

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

	public void showP1Ships() {
		Node[][] p1Grid = p1board.getGrid();
		
		for (int r = 0; r < p1board.size; r++) {
			for (int c = 0; c < p1board.size; c++) {
				if(p1Grid[r][c].getVal() == Node.SHIP) {
					p1view.getButton(r, c).setBackground(Color.DARK_GRAY);
					p1view.getButton(r, c).setText("Tàu");
					p1view.getButton(r, c).setForeground(Color.WHITE);
				}
				p1view.getButton(r,c).setEnabled(false);
			}
		}
	}

	private void setupMouseClicks() {
		for (int r = 0; r < Board.size; r++) {
			for (int c = 0; c < Board.size; c++) {
				final int row = r;
				final int col = c;

				p2view.getButton(row, col).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean isHit = p2board.fireAt(row, col);
						p2view.updateButtonState(row, col, isHit);
					}
				});
			}
		}
	}
}
