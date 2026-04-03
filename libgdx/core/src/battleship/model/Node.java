package battleship.model;

public class Node {
	private int x;
	private int y;
	private int val;

	public static final int EMPTY = 0;
	public static final int SHIP = 1;
	public static final int HIT = 2;
	public static final int MISS = 3;
	public static final int SUNK = 4;

	public Node(int x, int y, int val) {
		this.x = x;
		this.y = y;
		this.val = val;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
}
