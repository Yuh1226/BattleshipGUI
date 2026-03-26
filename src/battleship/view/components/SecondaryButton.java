package battleship.view.components;

import javax.swing.JButton;

import battleship.view.theme.UiTheme;

public class SecondaryButton extends JButton {
	public SecondaryButton(String text) {
		super(text);
		setFont(UiTheme.buttonFont());
		setForeground(UiTheme.NAVY);
		setBackground(UiTheme.SAND);
		setFocusPainted(false);
		setBorder(new RoundBorder(10, UiTheme.OLIVE, 2));
		setOpaque(true);
	}
}
