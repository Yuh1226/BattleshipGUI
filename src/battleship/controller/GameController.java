package battleship.controller;

import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;
import battleship.view.BoardPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameController {
	private Board modelBoard;
	private BoardPanel viewPanel;

	public GameController(Board modelBoard, BoardPanel viewPanel) {
		this.modelBoard = modelBoard;
		this.viewPanel = viewPanel;
		setUpShipP2(modelBoard);
		setupMouseClicks();
	}
	
	private void setupMouseClicks() {
		for (int r = 0; r < Board.size; r++) {
			for (int c = 0; c < Board.size; c++) {
				final int row = r;
				final int col = c;

				viewPanel.getButton(row, col).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean isHit = modelBoard.fireAt(row, col);
						viewPanel.updateButtonState(row, col, isHit);
					}
				});
			}
		}
	}
	
	public void setUpShipP2(Board modelBoard) {
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
}
