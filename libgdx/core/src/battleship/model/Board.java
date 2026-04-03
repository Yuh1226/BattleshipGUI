package battleship.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
	private Node[][] grid;
	public static final int size = 10;
	private final List<Ship> fleet = new ArrayList<>();

	public Board() {
		grid = new Node[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = new Node(i, j, Node.EMPTY);
			}
		}
	}

	public Node[][] getGrid() {
		return grid;
	}

	public void reset() {
		fleet.clear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j].setVal(Node.EMPTY);
			}
		}
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

	public boolean canPlaceShip(int length, int row, int col, int direction) {
		for (int i = 0; i < length; i++) {
			int r = row + (direction == Ship.VERTICAl ? i : 0);
			int c = col + (direction == Ship.HORIZONTAL ? i : 0);

			if (r < 0 || r >= 10 || c < 0 || c >= 10) {
				return false;
			}

			if (grid[r][c].getVal() == Node.SHIP) {
				return false;
			}
		}
		return true;
	}

	public void placeShip(Ship s) {
		int r = s.getRow();
		int c = s.getCol();
		for (int i = 0; i < s.getLength(); i++) {
			if (s.getDirection() == Ship.HORIZONTAL) {
				grid[r][c + i].setVal(Node.SHIP);
			} else {
				grid[r + i][c].setVal(Node.SHIP);
			}
		}
		fleet.add(s);
	}

	public boolean hasShips() {
		return !fleet.isEmpty();
	}

	public boolean fireAt(int row, int col) {
		Node target = grid[row][col];
		if (target.getVal() == Node.SHIP) {
			target.setVal(Node.HIT);
			return true;
		} else if (target.getVal() == Node.EMPTY) {
			target.setVal(Node.MISS);
		}
		return false;
	}

	public boolean isSunkAt(int row, int col) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();
			boolean isTargetShip = false;

			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					isTargetShip = true;
					break;
				}
			}

			if (isTargetShip) {
				for (int i = 0; i < s.getLength(); i++) {
					int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
					int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
					if (grid[checkR][checkC].getVal() != Node.HIT) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public void markShipAsSunk(int row, int col, Node[][] targetGrid) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();
			boolean isTargetShip = false;

			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					isTargetShip = true;
					break;
				}
			}

			if (isTargetShip) {
				for (int i = 0; i < s.getLength(); i++) {
					int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
					int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
					targetGrid[checkR][checkC].setVal(Node.SUNK);
				}
				break;
			}
		}
	}

	public int lengthShipIs(int row, int col) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();

			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					return s.getLength();
				}
			}
		}
		return 0;
	}

	public int countRemainingShips() {
		int remaining = 0;
		for (Ship s : fleet) {
			if (!isShipSunk(s)) {
				remaining++;
			}
		}
		return remaining;
	}

	private boolean isShipSunk(Ship s) {
		int r = s.getRow();
		int c = s.getCol();
		for (int i = 0; i < s.getLength(); i++) {
			int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
			int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
			int val = grid[checkR][checkC].getVal();
			if (val != Node.HIT && val != Node.SUNK) {
				return false;
			}
		}
		return true;
	}
}
