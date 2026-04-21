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

	public void reset() {
		fleet.clear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j].setVal(Node.EMPTY);
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

			if (r < 0 || r >= size || c < 0 || c >= size)
				return false;

			// Check current cell and all 8 surrounding cells for existing ships
			for (int dr = -1; dr <= 1; dr++) {
				for (int dc = -1; dc <= 1; dc++) {
					int nr = r + dr;
					int nc = c + dc;
					if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
						if (grid[nr][nc].getVal() == Node.SHIP) {
							return false;
						}
					}
				}
			}
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

	public Ship getShipAt(int row, int col) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();
			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					return s;
				}
			}
		}
		return null;
	}

	public void removeShip(Ship s) {
		if (!fleet.contains(s)) return;
		int r = s.getRow();
		int c = s.getCol();
		for (int i = 0; i < s.getLength(); i++) {
			if (s.getDirection() == Ship.HORIZONTAL) {
				grid[r][c + i].setVal(Node.EMPTY);
			} else {
				grid[r + i][c].setVal(Node.EMPTY);
			}
		}
		fleet.remove(s);
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

			// Ktra tàu đã bị bắn chưa
			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					isTargetShip = true;
					break;
				}
			}
			// Ktra tàu đã bị bắn hết chưa
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

	// Hàm biến các ô HIT thành SUNK khi tàu chìm
	public void markShipAsSunk(int row, int col, Node[][] p1Grid) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();
			boolean isTargetShip = false;

			// Tìm xem phát đạn (row, col) này thuộc về con tàu nào
			for (int i = 0; i < s.getLength(); i++) {
				int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
				int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
				if (checkR == row && checkC == col) {
					isTargetShip = true;
					break;
				}
			}

			// Nếu tìm thấy, đổi toàn bộ các ô của tàu đó thành SUNK
			if (isTargetShip) {
				for (int i = 0; i < s.getLength(); i++) {
					int checkR = r + (s.getDirection() == Ship.VERTICAl ? i : 0);
					int checkC = c + (s.getDirection() == Ship.HORIZONTAL ? i : 0);
					p1Grid[checkR][checkC].setVal(Node.SUNK);
				}
				break;
			}
		}
	}

	public int lengthShipIs(int row, int col) {
		for (Ship s : fleet) {
			int r = s.getRow();
			int c = s.getCol();

			// Ktra tàu đã bị bắn chưa
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