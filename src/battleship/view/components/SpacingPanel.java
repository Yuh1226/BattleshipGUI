package battleship.view.components;

import java.awt.Dimension;

import javax.swing.JPanel;

public class SpacingPanel extends JPanel {
	public SpacingPanel(int width, int height) {
		setOpaque(false);
		setPreferredSize(new Dimension(width, height));
	}
}
