package battleship.view.components;

import javax.swing.JLabel;

import battleship.view.theme.UiTheme;

public class TitleLabel extends JLabel {
	public TitleLabel(String text) {
		super(text, JLabel.CENTER);
		setFont(UiTheme.titleFont());
		setForeground(UiTheme.NAVY);
	}
}
