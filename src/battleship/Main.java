package battleship;

import javax.swing.*;
import java.awt.*;

import battleship.model.Board;
import battleship.view.BoardPanel;
import battleship.controller.GameController;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//1.Model
				Board p1board = new Board();
				Board p2board = new Board();
				
				//2.View
				BoardPanel p1view = new BoardPanel();
				BoardPanel p2view = new BoardPanel();
				
				//Cửa sổ
				JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JPanel p1Container = new JPanel(new BorderLayout());
                JLabel p1Label = new JLabel("HẠM ĐỘI CỦA BẠN", SwingConstants.CENTER);
                p1Label.setFont(new Font("Arial", Font.BOLD, 16));
                p1Container.add(p1Label, BorderLayout.NORTH);
                p1Container.add(p1view, BorderLayout.CENTER);

                JPanel p2Container = new JPanel(new BorderLayout());
                JLabel p2Label = new JLabel("VÙNG BIỂN ĐỊCH (BẮN VÀO ĐÂY)", SwingConstants.CENTER);
                p2Label.setFont(new Font("Arial", Font.BOLD, 16));
                p2Container.add(p2Label, BorderLayout.NORTH);
                p2Container.add(p2view, BorderLayout.CENTER);

                mainPanel.add(p1Container);
                mainPanel.add(p2Container);
				
				//3.Controller
				new GameController(p1board,p2board,p1view,p2view);
				
				//Tạo khung cửa sổ chứa bảng chứ cái
				JFrame frame = new JFrame("Game Đại Chiến Trên Biển");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(mainPanel);
				
				frame.setSize(1000,500);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
