package battleship.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	private Node[][] grid;

	public static final int size = 10;

	private List<Ship> fleet = new ArrayList<>();

	public Board() {
		grid = new Node[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = new Node(i, j, Node.EMPTY);
			}
		}
	}

	// Lấy ra grid
	public Node[][] getGrid() {
		return grid;
	}

	// Kiểm tra có thể đặt tàu không ?
	public boolean canPlaceShip(int length, int row, int col, int direction) {
		for (int i = 0; i < length; i++) {
			int r = row + (direction == Ship.VERTICAl ? i : 0);
			int c = col + (direction == Ship.HORIZONTAL ? i : 0);

			if (r < 0 || r >= 10 || c < 0 || c >= 10)
				return false;

			if (grid[r][c].getVal() == Node.SHIP)
				return false;
		}
		return true;
	}

	// Đặt tàu
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

	// Kiểm tra vị trí bắn
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
			
			//Ktra tàu đã bị bắn chưa
			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					isTargetShip = true;
					break;
				}
			}
			//Ktra tàu đã bị bắn hết chưa
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
	
	public int lengthShipIs(int row, int col) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();
			boolean isTargetShip = false;
			
			//Ktra tàu đã bị bắn chưa
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
	
	public static void main(String[] args) {
	}
}
