package battleship.model;

public class Ship {
	private int length;
	private int direction;
	private int row; // x
	private int col; // y

	// Hướng
	public static final int UNSET = -1;
	public static final int HORIZONTAL = 0;
	public static final int VERTICAl = 1;

	public Ship(int length) {
		this.length = length;
		this.row = -1;
		this.col = -1;
		this.direction = -1;
	}

	// ktra tàu có tọa độ chưa ?
	public boolean isLocationSet() {
		if (row == -1 || col == -1) {
			return false;
		} else {
			return true;
		}
	}

	// ktra tàu có hướng chưa ?
	public boolean isDirectionSet() {
		if (direction == UNSET) {
			return false;
		} else {
			return true;
		}
	}

	// Đặt tọa độ tàu
	public void setLocation(int row, int col) {
		this.row = row;
		this.col = col;
	}

	// Đặt hướng
	public void setDirection(int direction) {
		if (direction != UNSET && direction != HORIZONTAL && direction != VERTICAl) {
			throw new IllegalArgumentException("Huong khong hop le, Hay nhap lai(-1,0,1)");
		}
		this.direction = direction;
	}

	// Lấy hàng
	public int getRow() {
		return row;
	}

	// Lấy cột
	public int getCol() {
		return col;
	}

	// Lấy độ dài
	public int getLength() {
		return length;
	}

	// Lấy hướng
	public int getDirection() {
		return direction;
	}

	private String directionToString() {
		if (direction == UNSET)
			return "UNSET";
		else if (direction == HORIZONTAL)
			return "HORIZONTAL";
		else
			return "VERTICAL";
	}

	public String ToString() {
		return "Ship: " + getRow() + ", " + getCol() + " with length " + getLength() + " and direction "
				+ directionToString();
	}

	public static void main(String[] args) {
	}
}
