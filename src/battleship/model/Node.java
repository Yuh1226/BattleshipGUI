package battleship.model;

public class Node {
	private int x;
	private int y;
	private int val;

	public static final int EMPTY = 0; // Nước biển
	public static final int SHIP = 1; // Có tàu
	public static final int HIT = 2; // Tàu bị bắn trúng
	public static final int MISS = 3; // Bắn trượt xuống nước

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

//	public String toString() {
//		if (val == EMPTY) {
//			return " ";
//		} else if (val == SHIP) {
//			return "█";
//		} else if (val == HIT) {
//			return "X";
//		} else {
//			return "O";
//		}
//	}
}