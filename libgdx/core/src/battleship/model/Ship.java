package battleship.model;

public class Ship {
	private int length;
	private int direction;
	private int row;
	private int col;

	public static final int UNSET = -1;
	public static final int HORIZONTAL = 0;
	public static final int VERTICAl = 1;

	public Ship(int length) {
		this.length = length;
		this.row = -1;
		this.col = -1;
		this.direction = -1;
	}

	public boolean isLocationSet() {
		return row != -1 && col != -1;
	}

	public boolean isDirectionSet() {
		return direction != UNSET;
	}

	public void setLocation(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void setDirection(int direction) {
		if (direction != UNSET && direction != HORIZONTAL && direction != VERTICAl) {
			throw new IllegalArgumentException("Huong khong hop le, Hay nhap lai(-1,0,1)");
		}
		this.direction = direction;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getLength() {
		return length;
	}

	public int getDirection() {
		return direction;
	}
}
