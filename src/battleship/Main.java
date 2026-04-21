package battleship;

import battleship.ai.BattleshipAI;
import battleship.fx.FxGameController;
import battleship.fx.LocalizationManager;
import battleship.fx.ScreenManager;
import battleship.fx.screens.BattleScreen;
import battleship.fx.screens.DifficultyScreen;
import battleship.fx.screens.GameOverScreen;
import battleship.fx.screens.MenuScreen;
import battleship.fx.screens.SettingsScreen;
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
	public static final String SCREEN_SETTINGS = "settings";

	private MenuScreen menuScreen;
	private DifficultyScreen difficultyScreen;
	private SetupScreen setupScreen;
	private BattleScreen battleScreen;
	private GameOverScreen gameOverScreen;
	private SettingsScreen settingsScreen;
	private String previousScreen = SCREEN_MENU;
	private javafx.scene.control.Button globalSettingsBtn;

	@Override
	public void start(Stage stage) {
		ScreenManager screenManager = new ScreenManager();
		Board p1board = new Board();
		Board p2board = new Board();

		menuScreen = new MenuScreen();
		difficultyScreen = new DifficultyScreen();
		setupScreen = new SetupScreen();
		battleScreen = new BattleScreen();
		gameOverScreen = new GameOverScreen();
		settingsScreen = new SettingsScreen();
		FxGameController controller = new FxGameController(p1board, p2board, setupScreen, battleScreen);

		screenManager.addScreen(SCREEN_MENU, menuScreen);
		screenManager.addScreen(SCREEN_DIFFICULTY, difficultyScreen);
		screenManager.addScreen(SCREEN_SETUP, setupScreen);
		screenManager.addScreen(SCREEN_BATTLE, battleScreen);
		screenManager.addScreen(SCREEN_GAME_OVER, gameOverScreen);
		screenManager.addScreen(SCREEN_SETTINGS, settingsScreen);

		// Global Settings Button
		globalSettingsBtn = new javafx.scene.control.Button("⚙");
		globalSettingsBtn.getStyleClass().add("secondary-button");
		globalSettingsBtn.setStyle("-fx-font-size: 20px; -fx-padding: 5 10; -fx-background-radius: 40;");
		javafx.scene.layout.StackPane.setAlignment(globalSettingsBtn, javafx.geometry.Pos.TOP_RIGHT);
		javafx.scene.layout.StackPane.setMargin(globalSettingsBtn, new javafx.geometry.Insets(20));
		globalSettingsBtn.setOnAction(e -> openSettings(screenManager, currentScreenName(screenManager)));
		
		screenManager.getRoot().getChildren().add(globalSettingsBtn);

		menuScreen.setListener(new MenuScreen.Listener() {
			@Override
			public void onStart() {
				showScreen(screenManager, SCREEN_DIFFICULTY);
			}

			@Override
			public void onSettings() {
				openSettings(screenManager, SCREEN_MENU);
			}
		});

		settingsScreen.setListener(new SettingsScreen.Listener() {
			@Override
			public void onBack() {
				showScreen(screenManager, previousScreen);
			}

			@Override
			public void onBackToMenu() {
				showScreen(screenManager, SCREEN_MENU);
			}

			@Override
			public void onLanguageChanged(String lang) {
				updateAllScreensLanguage();
			}

			@Override
			public void onSoundToggled(boolean enabled) {
			}

			@Override
			public void onVolumeChanged(double volume) {
			}
		});

		difficultyScreen.setListener(new DifficultyScreen.Listener() {
			@Override
			public void onDifficultySelected(DifficultyScreen.Difficulty difficulty) {
				int aiLevel = BattleshipAI.HARD;
				switch (difficulty) {
					case EASY: aiLevel = BattleshipAI.EASY; break;
					case NORMAL: aiLevel = BattleshipAI.MEDIUM; break;
					default: aiLevel = BattleshipAI.HARD; break;
				}
				controller.setDifficulty(aiLevel);
				controller.resetSetup();
				showScreen(screenManager, SCREEN_SETUP);
			}

			@Override
			public void onBack() {
				showScreen(screenManager, SCREEN_MENU);
			}

			@Override
			public void onOpenSettings() {
				openSettings(screenManager, SCREEN_DIFFICULTY);
			}
		});

		setupScreen.addListener(new SetupScreen.Listener() {
			@Override
			public void onBack() {
				showScreen(screenManager, SCREEN_DIFFICULTY);
			}

			@Override
			public void onContinue() {
				controller.startBattle();
				showScreen(screenManager, SCREEN_BATTLE);
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

			@Override
			public void onOpenSettings() {
				openSettings(screenManager, SCREEN_SETUP);
			}
		});

		battleScreen.setListener(new BattleScreen.Listener() {
			@Override
			public void onOpenSettings() {
				openSettings(screenManager, SCREEN_BATTLE);
			}
		});

		gameOverScreen.setListener(new GameOverScreen.Listener() {
			@Override
			public void onPlayAgain() {
				controller.resetSetup();
				showScreen(screenManager, SCREEN_SETUP);
			}

			@Override
			public void onBackToMenu() {
				showScreen(screenManager, SCREEN_MENU);
			}

			@Override
			public void onOpenSettings() {
				openSettings(screenManager, SCREEN_GAME_OVER);
			}
		});

		controller.setGameEventListener((playerWon, shots, hits, sunk, duration, difficulty) -> {
			if (playerWon) {
				gameOverScreen.setResultText(LocalizationManager.get("win"));
			} else {
				gameOverScreen.setResultText(LocalizationManager.get("lose"));
			}
			gameOverScreen.setStats(shots, hits, sunk, duration, difficulty);
			showScreen(screenManager, SCREEN_GAME_OVER);
		});

		showScreen(screenManager, SCREEN_MENU);

		Scene scene = new Scene(screenManager.getRoot(), 960, 640);
		scene.getStylesheets().add(Main.class.getResource("/styles/app.css").toExternalForm());
		stage.setTitle("Battleship");
		stage.setScene(scene);
		stage.show();
	}

	private String currentScreen;
	private void showScreen(ScreenManager sm, String name) {
		this.currentScreen = name;
		sm.show(name);
		if (globalSettingsBtn != null) {
			globalSettingsBtn.setVisible(!SCREEN_SETTINGS.equals(name));
		}
	}

	private String currentScreenName(ScreenManager sm) {
		return currentScreen;
	}

	private void updateAllScreensLanguage() {
		menuScreen.updateLanguage();
		difficultyScreen.updateLanguage();
		setupScreen.updateLanguage();
		battleScreen.updateLanguage();
		gameOverScreen.updateLanguage();
		settingsScreen.updateLanguage();
	}

	private void openSettings(ScreenManager sm, String from) {
		this.previousScreen = from;
		sm.show(SCREEN_SETTINGS);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
