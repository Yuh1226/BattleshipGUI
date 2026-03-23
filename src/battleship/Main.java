package battleship;

import battleship.model.Board;
import battleship.view.BoardPanel;
import battleship.controller.GameController;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Tạo logic
				Board model = new Board();
				//Tạo giao diện 
				BoardPanel view = new BoardPanel();
				//Kết nối 2 cái bằng controller
				new GameController(model,view);
				
				//Tạo khung cửa sổ chứa bảng chứ cái
				JFrame frame = new JFrame("Game bắn tàu - java Swing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(view);
				
				frame.setSize(600,600);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
