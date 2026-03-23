package battleship.model;

import battleship.model.Node;
import java.util.Random;

public class Board {
	private Node[][] grid;

	public static final int size = 10;

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
	}

	public boolean fireAt(int row,int col) {
		Node target = grid[row][col];
		
		if(target.getVal() == Node.SHIP) {
			target.setVal(Node.HIT);
			return true;
		}
		else if(target.getVal() == Node.EMPTY) {
			target.setVal(Node.MISS);
		}
		return false;
	}

	public static void main(String[] args) {
	}
}
