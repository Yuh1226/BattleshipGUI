package battleship.view.screens;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import battleship.view.components.PrimaryButton;
import battleship.view.components.SecondaryButton;
import battleship.view.components.TitleLabel;
import battleship.view.theme.UiTheme;

public class DifficultyView extends JPanel {
	public enum Difficulty {
		EASY,
		MEDIUM,
		HARD
	}

	public interface Listener {
		void onDifficultySelected(Difficulty difficulty);
		void onBack();
	}

	private Listener listener;

	public DifficultyView() {
		setLayout(new BorderLayout());
		setBackground(UiTheme.SAND);

		TitleLabel title = new TitleLabel("BATTLESHIP");
		add(title, BorderLayout.NORTH);

		JPanel center = new JPanel(new GridBagLayout());
		center.setBackground(UiTheme.SAND);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.gridx = 0;

		JLabel label = new JLabel("Chon do kho", JLabel.CENTER);
		label.setFont(UiTheme.bodyFont());
		label.setForeground(UiTheme.NAVY);
		gbc.gridy = 0;
		center.add(label, gbc);

		gbc.gridy = 1;
		center.add(createDifficultyButton("De", Difficulty.EASY, false), gbc);

		gbc.gridy = 2;
		center.add(createDifficultyButton("Vua", Difficulty.MEDIUM, true), gbc);

		gbc.gridy = 3;
		center.add(createDifficultyButton("Kho", Difficulty.HARD, false), gbc);

		add(center, BorderLayout.CENTER);

		JPanel footer = new JPanel(new GridBagLayout());
		footer.setBackground(UiTheme.SAND);
		GridBagConstraints footerGbc = new GridBagConstraints();
		footerGbc.insets = new Insets(6, 6, 6, 6);
		SecondaryButton backButton = new SecondaryButton("Quay lai");
		backButton.addActionListener(e -> {
			if (listener != null) {
				listener.onBack();
			}
		});
		footer.add(backButton, footerGbc);
		add(footer, BorderLayout.SOUTH);
	}

	private JButton createDifficultyButton(String text, Difficulty difficulty, boolean isPrimary) {
		JButton button = isPrimary ? new PrimaryButton(text) : new SecondaryButton(text);
		button.addActionListener(e -> {
			if (listener != null) {
				listener.onDifficultySelected(difficulty);
			}
		});
		return button;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
}
