package battleship.view.components;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AppPanel extends JPanel {
	public AppPanel(Color background, Insets padding) {
		setBackground(background);
		setBorder(new EmptyBorder(padding));
	}
}
