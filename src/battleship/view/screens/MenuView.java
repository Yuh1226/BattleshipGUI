package battleship.view.screens;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import battleship.view.components.PrimaryButton;
import battleship.view.components.TitleLabel;
import battleship.view.theme.UiTheme;

public class MenuView extends JPanel {
	public interface Listener {
		void onStart();
	}

	private Listener listener;

	public MenuView() {
		setLayout(new BorderLayout());
		setBackground(UiTheme.SAND);

		TitleLabel title = new TitleLabel("BATTLESHIP");
		add(title, BorderLayout.NORTH);

		JPanel center = new JPanel(new GridBagLayout());
		center.setBackground(UiTheme.SAND);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);

		PrimaryButton startButton = new PrimaryButton("Bat dau game");
		startButton.addActionListener(e -> {
			if (listener != null) {
				listener.onStart();
			}
		});
		center.add(startButton, gbc);
		add(center, BorderLayout.CENTER);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
}
