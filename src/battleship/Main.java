package battleship;

import battleship.ai.BattleshipAI;
import battleship.fx.FxGameController;
import battleship.fx.ScreenManager;
import battleship.fx.screens.BattleScreen;
import battleship.fx.screens.DifficultyScreen;
import battleship.fx.screens.GameOverScreen;
import battleship.fx.screens.MenuScreen;
import battleship.fx.screens.SetupScreen;
import battleship.model.Board;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String SCREEN_MENU = "menu";
	public static final String SCREEN_DIFFICULTY = "difficulty";
	public static final String SCREEN_SETUP = "setup";
	public static final String SCREEN_BATTLE = "battle";
	public static final String SCREEN_GAME_OVER = "gameover";

	@Override
	public void start(Stage stage) {
		ScreenManager screenManager = new ScreenManager();
		Board p1board = new Board();
		Board p2board = new Board();

		MenuScreen menuScreen = new MenuScreen();
		DifficultyScreen difficultyScreen = new DifficultyScreen();
		SetupScreen setupScreen = new SetupScreen();
		BattleScreen battleScreen = new BattleScreen();
		GameOverScreen gameOverScreen = new GameOverScreen();
		FxGameController controller = new FxGameController(p1board, p2board, setupScreen, battleScreen);

		screenManager.addScreen(SCREEN_MENU, menuScreen);
		screenManager.addScreen(SCREEN_DIFFICULTY, difficultyScreen);
		screenManager.addScreen(SCREEN_SETUP, setupScreen);
		screenManager.addScreen(SCREEN_BATTLE, battleScreen);
		screenManager.addScreen(SCREEN_GAME_OVER, gameOverScreen);

		menuScreen.setListener(() -> screenManager.show(SCREEN_DIFFICULTY));
		difficultyScreen.setListener(new DifficultyScreen.Listener() {
			@Override
			public void onDifficultySelected(DifficultyScreen.Difficulty difficulty) {
				int aiLevel = BattleshipAI.HARD;
				switch (difficulty) {
					case EASY:
						aiLevel = BattleshipAI.EASY;
						break;
					case NORMAL:
						aiLevel = BattleshipAI.MEDIUM;
						break;
					default:
						aiLevel = BattleshipAI.HARD;
						break;
				}
				controller.setDifficulty(aiLevel);
				controller.resetSetup();
				screenManager.show(SCREEN_SETUP);
			}

			@Override
			public void onBack() {
				screenManager.show(SCREEN_MENU);
			}
		});
		setupScreen.addListener(new SetupScreen.Listener() {
			@Override
			public void onBack() {
				screenManager.show(SCREEN_DIFFICULTY);
			}

			@Override
			public void onContinue() {
				controller.startBattle();
				screenManager.show(SCREEN_BATTLE);
			}

			@Override
			public void onRotate() {
			}

			@Override
			public void onRandomize() {
			}

			@Override
			public void onReset() {
			}

			@Override
			public void onShipSelected(int length) {
			}
		});
		battleScreen.setListener(() -> screenManager.show(SCREEN_MENU));
		gameOverScreen.setListener(new GameOverScreen.Listener() {
			@Override
			public void onPlayAgain() {
				controller.resetSetup();
				screenManager.show(SCREEN_SETUP);
			}

			@Override
			public void onBackToMenu() {
				screenManager.show(SCREEN_MENU);
			}
		});
		controller.setGameEventListener(playerWon -> {
			if (playerWon) {
				gameOverScreen.setResultText("You win!");
			} else {
				gameOverScreen.setResultText("You lose.");
			}
			screenManager.show(SCREEN_GAME_OVER);
		});

		screenManager.show(SCREEN_MENU);

		Scene scene = new Scene(screenManager.getRoot(), 960, 640);
		scene.getStylesheets().add(Main.class.getResource("/styles/app.css").toExternalForm());
		stage.setTitle("Battleship");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
