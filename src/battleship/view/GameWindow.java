package battleship.view;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {
	public static final String SCREEN_MENU = "menu";
	public static final String SCREEN_DIFFICULTY = "difficulty";
	public static final String SCREEN_SETUP = "setup";
	public static final String SCREEN_BATTLE = "battle";

	private final CardLayout cardLayout;
	private final JPanel root;

	public GameWindow(String title) {
		super(title);
		this.cardLayout = new CardLayout();
		this.root = new JPanel(cardLayout);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(root);
		setSize(1024, 640);
		setLocationRelativeTo(null);
	}

	public void addScreen(String name, JPanel panel) {
		root.add(panel, name);
	}

	public void showScreen(String name) {
		cardLayout.show(root, name);
	}
}
