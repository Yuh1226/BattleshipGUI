package battleship.view.screens;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import battleship.view.BoardPanel;
import battleship.view.theme.UiTheme;

public class BattleView extends JPanel {
	private final BoardPanel playerBoard;
	private final BoardPanel enemyBoard;

	public BattleView(BoardPanel playerBoard, BoardPanel enemyBoard) {
		this.playerBoard = playerBoard;
		this.enemyBoard = enemyBoard;

		setLayout(new BorderLayout());
		setBackground(UiTheme.SAND);
		add(buildBoardsPanel(), BorderLayout.CENTER);
	}

	private JPanel buildBoardsPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setBackground(UiTheme.SAND);

		JPanel p1Container = new JPanel(new BorderLayout());
		p1Container.setBackground(UiTheme.SAND);
		JLabel p1Label = new JLabel("HAM DOI CUA BAN", SwingConstants.CENTER);
		p1Label.setFont(UiTheme.bodyFont());
		p1Label.setForeground(UiTheme.NAVY);
		p1Container.add(p1Label, BorderLayout.NORTH);
		p1Container.add(playerBoard, BorderLayout.CENTER);

		JPanel p2Container = new JPanel(new BorderLayout());
		p2Container.setBackground(UiTheme.SAND);
		JLabel p2Label = new JLabel("VUNG BIEN DICH", SwingConstants.CENTER);
		p2Label.setFont(UiTheme.bodyFont());
		p2Label.setForeground(UiTheme.NAVY);
		p2Container.add(p2Label, BorderLayout.NORTH);
		p2Container.add(enemyBoard, BorderLayout.CENTER);

		panel.add(p1Container);
		panel.add(p2Container);

		return panel;
	}

	public BoardPanel getPlayerBoard() {
		return playerBoard;
	}

	public BoardPanel getEnemyBoard() {
		return enemyBoard;
	}
}
