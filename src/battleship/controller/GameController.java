package battleship.controller;

import java.util.*;
import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;
import battleship.view.BoardPanel;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController {
	private Board p1board;
	private Board p2board;
	private BoardPanel p1view;
	private BoardPanel p2view;

	private int p1Hits = 0;
	private int p2Hits = 0;
	private final int Win_Score = 17;
	private boolean isGameOver = false;
	private Stack<Node> hitLocation = new Stack<>();
	private int dirShip = Ship.UNSET;

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
						} else {
							if (!isGameOver) {
//								botFire_EASY();
								botFire_MEDIUM();
							}
						}
					}
				});
			}
		}
	}

	private void botFire_EASY() {
		Random rd = new Random();
		int r, c;
		Node targetNode;
		Node[][] p1Grid = p1board.getGrid();
		boolean isHit;

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
			botFire_EASY();
		}
	}

	public int guessDirShip(Node[][] p1Grid) {
		if (hitLocation.size() < 2) {
			return Ship.UNSET;
		}

		Node lastHit = hitLocation.get(hitLocation.size() - 1);
		Node firstHit = hitLocation.get(0);

		if (lastHit.getX() == firstHit.getX()) {
			return Ship.HORIZONTAL;
		} else if (lastHit.getY() == firstHit.getY()) {
			return Ship.VERTICAl;
		}

		return Ship.UNSET;
	}

	public Node guessShip(Node[][] p1Grid, Node targetNode) {
		List<Node> validTargets = new ArrayList<>();
		int r = targetNode.getX();
		int c = targetNode.getY();

		if (r - 1 >= 0 && p1Grid[r - 1][c].getVal() != Node.HIT && p1Grid[r - 1][c].getVal() != Node.MISS) {
			validTargets.add(p1Grid[r - 1][c]);
		}
		if (r + 1 < 10 && p1Grid[r + 1][c].getVal() != Node.HIT && p1Grid[r + 1][c].getVal() != Node.MISS) {
			validTargets.add(p1Grid[r + 1][c]);
		}
		if (c - 1 >= 0 && p1Grid[r][c - 1].getVal() != Node.HIT && p1Grid[r][c - 1].getVal() != Node.MISS) {
			validTargets.add(p1Grid[r][c - 1]);
		}
		if (c + 1 < 10 && p1Grid[r][c + 1].getVal() != Node.HIT && p1Grid[r][c + 1].getVal() != Node.MISS) {
			validTargets.add(p1Grid[r][c + 1]);
		}

		if (!validTargets.isEmpty()) {
			Random rd = new Random();
			int randomIndex = rd.nextInt(validTargets.size());
			Node selectedNode = validTargets.get(randomIndex);
			return selectedNode;
		}
		return null;
	}

	public Node fireAlongDirection(Node[][] p1Grid) {
		Node lastHit = hitLocation.peek();
		int r = lastHit.getX();
		int c = lastHit.getY();
		List<Node> validTargets = new ArrayList<>();

		if (dirShip == Ship.HORIZONTAL) {
			// Ktra trái phải
			if (c - 1 >= 0 && p1Grid[r][c - 1].getVal() != Node.HIT && p1Grid[r][c - 1].getVal() != Node.MISS) {
				validTargets.add(p1Grid[r][c - 1]);
			}
			if (c + 1 < 10 && p1Grid[r][c + 1].getVal() != Node.HIT && p1Grid[r][c + 1].getVal() != Node.MISS) {
				validTargets.add(p1Grid[r][c + 1]);
			}
		} else if (dirShip == Ship.VERTICAl) {
			// Ktra trên dưới
			if (r - 1 >= 0 && p1Grid[r - 1][c].getVal() != Node.HIT && p1Grid[r - 1][c].getVal() != Node.MISS) {
				validTargets.add(p1Grid[r - 1][c]);
			}
			if (r + 1 < 10 && p1Grid[r + 1][c].getVal() != Node.HIT && p1Grid[r + 1][c].getVal() != Node.MISS) {
				validTargets.add(p1Grid[r + 1][c]);
			}
		}

		if (!validTargets.isEmpty()) {
			return validTargets.get(0);
		} else {
			Node firstHit = hitLocation.get(0);

			hitLocation.push(firstHit);

			Node fallBack = guessShip(p1Grid, firstHit);
			if (fallBack == null) {
				clearBotMemory();
				return getRandomNode(p1Grid);
			}
			return fallBack;
		}
	}

	private Node getRandomNode(Node[][] p1Grid) {
		Random rd = new Random();
		int r, c;
		Node targetNode;
		do {
			r = rd.nextInt(10);
			c = rd.nextInt(10);
			targetNode = p1Grid[r][c];
		} while (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS);
		return targetNode;
	}

	private void clearBotMemory() {
		hitLocation.clear();
		dirShip = Ship.UNSET;
	}

	private void botFire_MEDIUM() {
		if (isGameOver)
			return;

		Node targetNode = null;
		Node[][] p1Grid = p1board.getGrid();

		if (hitLocation.isEmpty()) {
			targetNode = getRandomNode(p1Grid);
		} else {
			if (dirShip == Ship.UNSET) {
				targetNode = guessShip(p1Grid, hitLocation.peek());
				if (targetNode == null) {
					clearBotMemory();
					targetNode = getRandomNode(p1Grid);
				}
			} else {
				targetNode = fireAlongDirection(p1Grid);
			}
		}

		int r = targetNode.getX();
		int c = targetNode.getY();
		boolean isHit = p1board.fireAt(r, c);
		p1view.updateButtonState(r, c, isHit);

		if (isHit) {
			p2Hits++;
			checkWinCondition();

			hitLocation.push(targetNode);
			
			if(dirShip == Ship.UNSET) {
				dirShip = guessDirShip(p1Grid);
			}

			if(!isGameOver) {
				botFire_MEDIUM();
			}
		}
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
