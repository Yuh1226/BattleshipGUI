package battleship.view.components;

import javax.swing.JButton;

import battleship.view.theme.UiTheme;

public class PrimaryButton extends JButton {
	public PrimaryButton(String text) {
		super(text);
		setFont(UiTheme.buttonFont());
		setForeground(UiTheme.OFF_WHITE);
		setBackground(UiTheme.ACCENT);
		setFocusPainted(false);
		setBorder(new RoundBorder(10, UiTheme.NAVY, 2));
		setOpaque(true);
	}
}
