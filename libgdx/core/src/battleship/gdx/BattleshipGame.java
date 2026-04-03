package battleship.gdx;

import java.util.EnumMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import battleship.gdx.screens.BattleScreen;
import battleship.gdx.screens.DifficultyScreen;
import battleship.gdx.screens.GameOverScreen;
import battleship.gdx.screens.MenuScreen;
import battleship.gdx.screens.SettingsScreen;
import battleship.gdx.screens.SetupScreen;
import battleship.model.Board;

public class BattleshipGame extends Game {
	public enum ScreenId {
		MENU,
		DIFFICULTY,
		SETUP,
		BATTLE,
		SETTINGS,
		GAME_OVER
	}

	private final EnumMap<ScreenId, Screen> screens = new EnumMap<>(ScreenId.class);
	private Board playerBoard;
	private Board enemyBoard;
	private int difficulty = battleship.ai.BattleshipAI.MEDIUM;
	private boolean battleResetPending = false;
	private boolean lastWin = true;
	private int lastShots = 0;
	private int lastHits = 0;
	private int lastSunk = 0;

	@Override
	public void create() {
		playerBoard = new Board();
		enemyBoard = new Board();
		screens.put(ScreenId.MENU, new MenuScreen(this));
		screens.put(ScreenId.DIFFICULTY, new DifficultyScreen(this));
		screens.put(ScreenId.SETUP, new SetupScreen(this));
		screens.put(ScreenId.BATTLE, new BattleScreen(this));
		screens.put(ScreenId.SETTINGS, new SettingsScreen(this));
		screens.put(ScreenId.GAME_OVER, new GameOverScreen(this));
		showScreen(ScreenId.MENU);
	}

	public Board getPlayerBoard() {
		return playerBoard;
	}

	public Board getEnemyBoard() {
		return enemyBoard;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public void prepareBattle() {
		battleResetPending = true;
		enemyBoard.reset();
		enemyBoard.setUpShip(enemyBoard);
	}

	public boolean consumeBattleReset() {
		if (battleResetPending) {
			battleResetPending = false;
			return true;
		}
		return false;
	}

	public void showGameOver(boolean playerWon, int shots, int hits, int sunk) {
		lastWin = playerWon;
		lastShots = shots;
		lastHits = hits;
		lastSunk = sunk;
		GameOverScreen screen = (GameOverScreen) screens.get(ScreenId.GAME_OVER);
		screen.updateResult(lastWin, lastShots, lastHits, lastSunk);
		showScreen(ScreenId.GAME_OVER);
	}

	public void resetBoards() {
		playerBoard.reset();
		enemyBoard.reset();
	}

	public void showScreen(ScreenId id) {
		setScreen(screens.get(id));
	}

	@Override
	public void dispose() {
		for (Screen screen : screens.values()) {
			if (screen != null) {
				screen.dispose();
			}
		}
		super.dispose();
	}
}
