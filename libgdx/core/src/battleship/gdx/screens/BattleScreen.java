package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;
import battleship.gdx.actors.BoardActor;
import battleship.gdx.actors.DashedRectActor;
import battleship.gdx.actors.TriangleActor;
import battleship.ai.BattleshipAI;
import battleship.model.Board;
import battleship.model.Node;

public class BattleScreen extends BaseScreen {
	private final BoardActor playerBoard;
	private final BoardActor enemyBoard;
	private final DashedRectActor leftBox;
	private final DashedRectActor rightBox;
	private final DashedRectActor statusBox;
	private final DashedRectActor leftBadge;
	private final DashedRectActor rightBadge;
	private final TriangleActor triangle;
	private final Board playerModel;
	private final Board enemyModel;
	private final Label statusLabel;
	private int playerHits = 0;
	private int playerShots = 0;
	private int playerSunk = 0;
	private int botHits = 0;
	private boolean isGameOver = false;
	private boolean isBotThinking = false;
	private static final int WIN_SCORE = 17;
	private BattleshipAI botAI = new BattleshipAI();
	private java.util.List<Integer> playerAliveShips = new java.util.ArrayList<>(
			java.util.Arrays.asList(5, 4, 3, 3, 2));
	private final Color playerShotColor = new Color(0.15f, 0.15f, 0.15f, 1f);
	private final Color botShotColor = new Color(0.70f, 0.10f, 0.10f, 1f);

	public BattleScreen(BattleshipGame game) {
		super(game);

		playerModel = game.getPlayerBoard();
		enemyModel = game.getEnemyBoard();
		playerBoard = new BoardActor(10, 24f, 2f);
		enemyBoard = new BoardActor(10, 24f, 2f);
		enemyBoard.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				handlePlayerFire(x, y);
				return true;
			}
		});

		Table root = new Table();
		root.setFillParent(true);
		root.top().pad(16);

		TextButton gear = new TextButton("G", styles.outlineButtonSmall);
		gear.addListener(styles.clickListener(() -> game.showScreen(ScreenId.SETTINGS)));
		Table header = new Table();
		header.add().expandX();
		header.add(gear).width(28).height(28);

		Label title = new Label("Tran dau", styles.titleStyle);
		TextButton back = new TextButton("Ve menu", styles.outlineButtonSmall);
		back.addListener(styles.clickListener(() -> game.showScreen(ScreenId.MENU)));

		float boardBoxPadding = 10f;
		float boardBoxWidth = playerBoard.getWidth() + boardBoxPadding * 2f;
		float boardBoxHeight = playerBoard.getHeight() + boardBoxPadding * 2f;

		Stack leftStack = new Stack();
		leftBox = new DashedRectActor(boardBoxWidth, boardBoxHeight, 6f, 4f, Color.BLACK);
		Table leftInner = new Table();
		leftInner.add(playerBoard).pad(boardBoxPadding);
		leftStack.setSize(boardBoxWidth, boardBoxHeight);
		leftStack.add(leftBox);
		leftStack.add(leftInner);

		Stack rightStack = new Stack();
		rightBox = new DashedRectActor(boardBoxWidth, boardBoxHeight, 6f, 4f, Color.BLACK);
		Table rightInner = new Table();
		rightInner.add(enemyBoard).pad(boardBoxPadding);
		rightStack.setSize(boardBoxWidth, boardBoxHeight);
		rightStack.add(rightBox);
		rightStack.add(rightInner);

		triangle = new TriangleActor(36f, 36f, Color.BLACK);

		Table boards = new Table();
		boards.add(leftStack).padRight(20);
		boards.add(triangle).padRight(20);
		boards.add(rightStack);

		float statusWidth = boardBoxWidth * 2f + 120f;
		float statusHeight = 36f;
		statusBox = new DashedRectActor(statusWidth, statusHeight, 6f, 4f, Color.BLACK);
		Stack statusStack = new Stack();
		statusStack.setSize(statusWidth, statusHeight);
		Table statusInner = new Table();
		statusLabel = new Label("Do kho: Vua | Luot choi: Nguoi | Tau ban: 5 | Tau doi thu: 5", styles.smallStyle);
		statusInner.add(statusLabel);
		statusStack.add(statusBox);
		statusStack.add(statusInner);

		Table badges = new Table();
		leftBadge = new DashedRectActor(32f, 32f, 6f, 4f, Color.BLACK);
		rightBadge = new DashedRectActor(32f, 32f, 6f, 4f, Color.BLACK);
		badges.add(leftBadge).padRight(8);
		badges.add(statusStack).padRight(8);
		badges.add(rightBadge);

		root.add(header).expandX().fillX().row();
		root.add(title).padBottom(8).row();
		root.add(badges).padBottom(12).row();
		root.add(boards).padBottom(12).row();
		root.add(back).width(140).height(30).row();
		stage.addActor(root);

		syncBoards();
	}

	@Override
	public void show() {
		super.show();
		if (game.consumeBattleReset()) {
			resetBattleState();
		}
		if (!enemyModel.hasShips()) {
			enemyModel.setUpShip(enemyModel);
		}
		if (!playerModel.hasShips()) {
			playerModel.setUpShip(playerModel);
		}
		syncBoards();
		updateStatus();
	}

	private void handlePlayerFire(float x, float y) {
		if (isGameOver || isBotThinking) {
			return;
		}
		int[] cell = enemyBoard.cellAt(x, y);
		if (cell == null) {
			return;
		}
		int row = cell[0];
		int col = cell[1];
		int current = enemyModel.getGrid()[row][col].getVal();
		if (current == Node.HIT || current == Node.MISS || current == Node.SUNK) {
			return;
		}
		boolean isHit = enemyModel.fireAt(row, col);
		playerShots++;
		if (isHit) {
			playerHits++;
			if (enemyModel.isSunkAt(row, col)) {
				enemyModel.markShipAsSunk(row, col, enemyModel.getGrid());
				playerSunk++;
			}
			if (playerHits >= WIN_SCORE) {
				isGameOver = true;
				game.showGameOver(true, playerShots, playerHits, playerSunk);
				return;
			}
		}
		if (!isHit && !isGameOver) {
			delayedBotFire();
		}
		enemyBoard.setHighlight(row, col, playerShotColor);
		syncBoards();
		updateStatus();
	}

	private void delayedBotFire() {
		isBotThinking = true;
		Timer.schedule(new Task() {
			@Override
			public void run() {
				botFire();
			}
		}, 0.8f);
	}

	private void botFire() {
		if (isGameOver) {
			isBotThinking = false;
			return;
		}
		int difficulty = game.getDifficulty();
		Node targetNode = botAI.determineTarget(playerModel, difficulty, playerAliveShips);
		int r = targetNode.getX();
		int c = targetNode.getY();
		boolean isHit = playerModel.fireAt(r, c);
		playerBoard.setHighlight(r, c, botShotColor);
		if (isHit) {
			botHits++;
			if (difficulty != BattleshipAI.EASY) {
				botAI.updateAfterFire(playerModel, targetNode, true);
			}
			if (playerModel.isSunkAt(r, c)) {
				int lengthShip = playerModel.lengthShipIs(r, c);
				playerAliveShips.remove(Integer.valueOf(lengthShip));
				playerModel.markShipAsSunk(r, c, playerModel.getGrid());
			}
			if (botHits >= WIN_SCORE) {
				isGameOver = true;
				game.showGameOver(false, playerShots, playerHits, playerSunk);
				return;
			}
			syncBoards();
			updateStatus();
			Timer.schedule(new Task() {
				@Override
				public void run() {
					botFire();
				}
			}, difficulty == BattleshipAI.EASY ? 0.4f : 0.7f);
			return;
		}
		isBotThinking = false;
		syncBoards();
		updateStatus();
	}

	private void syncBoards() {
		playerBoard.loadFromBoard(playerModel, true);
		enemyBoard.loadFromBoard(enemyModel, false);
	}

	private void updateStatus() {
		String difficultyLabel = "De";
		int diff = game.getDifficulty();
		if (diff == BattleshipAI.MEDIUM) {
			difficultyLabel = "Vua";
		} else if (diff == BattleshipAI.HARD) {
			difficultyLabel = "Kho";
		}
		String turn = isBotThinking ? "May" : "Nguoi";
		int playerShips = playerModel.countRemainingShips();
		int enemyShips = enemyModel.countRemainingShips();
		statusLabel.setText("Do kho: " + difficultyLabel + " | Luot choi: " + turn
				+ " | Tau ban: " + playerShips + " | Tau doi thu: " + enemyShips);
	}

	private void resetBattleState() {
		playerHits = 0;
		playerShots = 0;
		playerSunk = 0;
		botHits = 0;
		isGameOver = false;
		isBotThinking = false;
		botAI = new BattleshipAI();
		playerAliveShips = new java.util.ArrayList<>(java.util.Arrays.asList(5, 4, 3, 3, 2));
		playerBoard.clearHighlight();
		enemyBoard.clearHighlight();
	}

	@Override
	public void dispose() {
		playerBoard.dispose();
		enemyBoard.dispose();
		leftBox.dispose();
		rightBox.dispose();
		statusBox.dispose();
		leftBadge.dispose();
		rightBadge.dispose();
		triangle.dispose();
		super.dispose();
	}
}
