package battleship.view;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton[][] buttons;
	public static final int SIZE = 10;

	public BoardPanel() {
		setLayout(new GridLayout(SIZE, SIZE));
		buttons = new JButton[SIZE][SIZE];

		for (int r = 0; r < SIZE; r++) {
			for (int c = 0; c < SIZE; c++) {
				buttons[r][c] = new JButton();
				buttons[r][c].setBackground(new Color(30, 144, 255));
				buttons[r][c].setFocusPainted(false);
				add(buttons[r][c]);
			}
		}
	}

	// Hàm để đổi màu nút bấm khi biết kết quả Trúng hay Trượt
	public void updateButtonState(int row, int col, boolean isHit) {
		if (isHit) {
			buttons[row][col].setBackground(Color.RED); // Cháy tàu
			buttons[row][col].setText("X");
			buttons[row][col].setForeground(Color.WHITE);
		} else {
			buttons[row][col].setBackground(Color.LIGHT_GRAY); // Bom rơi tóm xuống biển
			buttons[row][col].setText("O");
		}
		buttons[row][col].setEnabled(false); // Bắn rồi thì khóa nút lại, cấm bấm tiếp
	}

	
	public JButton getButton(int row, int col) {
		return buttons[row][col];
	}
	
	public void markButtonAsSunk(int row, int col) {
		buttons[row][col].setBackground(Color.BLACK); 
		buttons[row][col].setText("☠"); 
		buttons[row][col].setForeground(Color.WHITE);
	}
	
	public static void main(String[] args) {
	}
}
