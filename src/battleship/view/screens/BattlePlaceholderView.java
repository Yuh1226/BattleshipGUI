package battleship.view.screens;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import battleship.view.theme.UiTheme;

public class BattlePlaceholderView extends JPanel {
	public BattlePlaceholderView() {
		setLayout(new BorderLayout());
		setBackground(UiTheme.SAND);
		JLabel label = new JLabel("Battle screen placeholder", JLabel.CENTER);
		label.setFont(UiTheme.bodyFont());
		label.setForeground(UiTheme.NAVY);
		add(label, BorderLayout.CENTER);
	}
}
