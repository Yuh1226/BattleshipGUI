package battleship.view.screens;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

import battleship.view.BoardPanel;
import battleship.view.components.PrimaryButton;
import battleship.view.components.SecondaryButton;
import battleship.view.components.TitleLabel;
import battleship.view.theme.UiTheme;

public class SetupView extends JPanel {
	public interface Listener {
		void onBack();
		void onContinue();
	}

	private Listener listener;
	private final BoardPanel setupBoard;

	public SetupView(BoardPanel setupBoard) {
		this.setupBoard = setupBoard;

		setLayout(new BorderLayout());
		setBackground(UiTheme.SAND);

		TitleLabel title = new TitleLabel("Dat tau");
		add(title, BorderLayout.NORTH);

		JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
		body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		body.setBackground(UiTheme.SAND);

		JPanel boardPanel = new JPanel(new BorderLayout());
		boardPanel.setBackground(UiTheme.SAND);
		boardPanel.add(setupBoard, BorderLayout.CENTER);

		JPanel rightPanel = buildShipPanel();
		body.add(boardPanel);
		body.add(rightPanel);
		add(body, BorderLayout.CENTER);

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

		PrimaryButton continueButton = new PrimaryButton("Bat dau tran");
		continueButton.addActionListener(e -> {
			if (listener != null) {
				listener.onContinue();
			}
		});

		footer.add(backButton, footerGbc);
		footer.add(continueButton, footerGbc);
		add(footer, BorderLayout.SOUTH);
	}

	private JPanel buildShipPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(UiTheme.SAND);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;

		JLabel shipsLabel = new JLabel("Danh sach tau");
		shipsLabel.setFont(UiTheme.bodyFont());
		shipsLabel.setForeground(UiTheme.NAVY);
		gbc.gridy = 0;
		panel.add(shipsLabel, gbc);

		gbc.gridy = 1;
		panel.add(new JCheckBox("Tau 5"), gbc);
		gbc.gridy = 2;
		panel.add(new JCheckBox("Tau 4"), gbc);
		gbc.gridy = 3;
		panel.add(new JCheckBox("Tau 3"), gbc);
		gbc.gridy = 4;
		panel.add(new JCheckBox("Tau 2"), gbc);

		JLabel directionLabel = new JLabel("Huong dat");
		directionLabel.setFont(UiTheme.bodyFont());
		directionLabel.setForeground(UiTheme.NAVY);
		gbc.gridy = 5;
		panel.add(directionLabel, gbc);

		JRadioButton horizontal = new JRadioButton("Ngang");
		horizontal.setBackground(UiTheme.SAND);
		JRadioButton vertical = new JRadioButton("Doc");
		vertical.setBackground(UiTheme.SAND);
		ButtonGroup group = new ButtonGroup();
		group.add(horizontal);
		group.add(vertical);
		horizontal.setSelected(true);

		gbc.gridy = 6;
		panel.add(horizontal, gbc);
		gbc.gridy = 7;
		panel.add(vertical, gbc);

		JPanel actions = new JPanel(new GridBagLayout());
		actions.setBackground(UiTheme.SAND);
		GridBagConstraints actionGbc = new GridBagConstraints();
		actionGbc.insets = new Insets(4, 4, 4, 4);
		actions.add(new SecondaryButton("Dat ngau nhien"), actionGbc);
		actions.add(new SecondaryButton("Xoa tat ca"), actionGbc);
		gbc.gridy = 8;
		panel.add(actions, gbc);

		return panel;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public BoardPanel getSetupBoard() {
		return setupBoard;
	}
}
