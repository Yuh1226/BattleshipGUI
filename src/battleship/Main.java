package battleship;

import javax.swing.*;
import java.awt.*;

import battleship.model.Board;
import battleship.view.BoardPanel;
import battleship.view.GameWindow;
import battleship.view.screens.BattleView;
import battleship.view.screens.DifficultyView;
import battleship.view.screens.MenuView;
import battleship.view.screens.SetupView;
import battleship.controller.GameController;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// 1.Model
			Board p1board = new Board();
			Board p2board = new Board();

			// 2.View
			BoardPanel p1view = new BoardPanel();
			BoardPanel p2view = new BoardPanel();
			BoardPanel setupBoard = new BoardPanel();
			BattleView battleView = new BattleView(p1view, p2view);
			MenuView menuView = new MenuView();
			DifficultyView difficultyView = new DifficultyView();
			SetupView setupView = new SetupView(setupBoard);

			// 3.Controller
			new GameController(p1board, p2board, battleView.getPlayerBoard(), battleView.getEnemyBoard());

			GameWindow window = new GameWindow("Game Dai Chien Tren Bien");
			window.addScreen(GameWindow.SCREEN_MENU, menuView);
			window.addScreen(GameWindow.SCREEN_DIFFICULTY, difficultyView);
			window.addScreen(GameWindow.SCREEN_SETUP, setupView);
			window.addScreen(GameWindow.SCREEN_BATTLE, battleView);
			window.showScreen(GameWindow.SCREEN_MENU);

			menuView.setListener(() -> window.showScreen(GameWindow.SCREEN_DIFFICULTY));
			difficultyView.setListener(new DifficultyView.Listener() {
				@Override
				public void onDifficultySelected(DifficultyView.Difficulty difficulty) {
					window.showScreen(GameWindow.SCREEN_SETUP);
				}

				@Override
				public void onBack() {
					window.showScreen(GameWindow.SCREEN_MENU);
				}
			});
			setupView.setListener(new SetupView.Listener() {
				@Override
				public void onBack() {
					window.showScreen(GameWindow.SCREEN_DIFFICULTY);
				}

				@Override
				public void onContinue() {
					window.showScreen(GameWindow.SCREEN_BATTLE);
				}
			});
			window.setVisible(true);
		});
	}
}
