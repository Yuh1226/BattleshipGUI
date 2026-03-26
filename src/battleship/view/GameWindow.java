package battleship.view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final String SCREEN_MENU = "menu";
	public static final String SCREEN_DIFFICULTY = "difficulty";
	public static final String SCREEN_SETUP = "setup";
	public static final String SCREEN_BATTLE = "battle";

	private final CardLayout cardLayout;
	private final JPanel cardPanel;

	public GameWindow(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(960, 720));
		setLocationRelativeTo(null);
		setResizable(true);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		setContentPane(cardPanel);
	}

	public void addScreen(String key, JComponent component) {
		cardPanel.add(component, key);
	}

	public void showScreen(String key) {
		cardLayout.show(cardPanel, key);
	}
}