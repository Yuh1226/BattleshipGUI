package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.Color;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;
import battleship.gdx.actors.BoardActor;
import battleship.gdx.actors.DashedRectActor;
import battleship.model.Board;
import battleship.model.Ship;

public class SetupScreen extends BaseScreen {
	private final BoardActor setupBoard;
	private final DashedRectActor boardBox;
	private final DashedRectActor sideBox;
	private final Board playerBoard;
	private final int[] shipLengths = { 5, 4, 3, 3, 2 };
	private final TextButton[] shipButtons = new TextButton[shipLengths.length];
	private int selectedShipIndex = -1;
	private int currentDirection = Ship.HORIZONTAL;
	private final TextButton continueBtn;

	public SetupScreen(BattleshipGame game) {
		super(game);

		playerBoard = game.getPlayerBoard();
		setupBoard = new BoardActor(10, 24f, 2f);
		setupBoard.loadFromBoard(playerBoard, true);
		setupBoard.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				int[] cell = setupBoard.cellAt(x, y);
				if (cell != null) {
					tryPlaceShip(cell[0], cell[1]);
				}
				return true;
			}
		});

		Table root = new Table();
		root.setFillParent(true);
		root.top().pad(20);

		TextButton gear = new TextButton("G", styles.outlineButtonSmall);
		gear.addListener(styles.clickListener(() -> game.showScreen(ScreenId.SETTINGS)));
		Table header = new Table();
		header.add().expandX();
		header.add(gear).width(28).height(28);

		Label title = new Label("Dat tau", styles.titleStyle);
		continueBtn = new TextButton("Bat dau tran", styles.outlineButton);
		TextButton back = new TextButton("Quay lai", styles.outlineButtonSmall);

		continueBtn.addListener(styles.clickListener(() -> {
			if (!allShipsPlaced()) {
				return;
			}
			game.prepareBattle();
			game.showScreen(ScreenId.BATTLE);
		}));
		back.addListener(styles.clickListener(() -> game.showScreen(ScreenId.DIFFICULTY)));

		float boardBoxPadding = 10f;
		float boardBoxWidth = setupBoard.getWidth() + boardBoxPadding * 2f;
		float boardBoxHeight = setupBoard.getHeight() + boardBoxPadding * 2f;
		boardBox = new DashedRectActor(boardBoxWidth, boardBoxHeight, 6f, 4f, Color.BLACK);
		Stack boardStack = new Stack();
		boardStack.setSize(boardBoxWidth, boardBoxHeight);
		Table boardInner = new Table();
		boardInner.add(setupBoard).pad(boardBoxPadding);
		boardStack.add(boardBox);
		boardStack.add(boardInner);

		float sideBoxWidth = 260f;
		float sideBoxHeight = boardBoxHeight;
		sideBox = new DashedRectActor(sideBoxWidth, sideBoxHeight, 6f, 4f, Color.BLACK);
		Stack sideStack = new Stack();
		sideStack.setSize(sideBoxWidth, sideBoxHeight);
		Table sideInner = new Table();
		sideInner.pad(14);
		sideInner.top().left();
		sideInner.add(new Label("Danh sach tau", styles.labelStyle)).left().padBottom(6).row();
		for (int i = 0; i < shipLengths.length; i++) {
			TextButton shipButton = new TextButton("[ ] Tau " + shipLengths[i] + " o", styles.outlineButtonSmall);
			int index = i;
			shipButton.addListener(styles.clickListener(() -> selectShip(index)));
			shipButtons[i] = shipButton;
			sideInner.add(shipButton).left().width(140).height(28).padBottom(4).row();
		}
		sideInner.add(new Label("Huong dat", styles.labelStyle)).left().padTop(6).padBottom(6).row();
		Table directionRow = new Table();
		TextButton horizontal = new TextButton("Ngang", styles.outlineButtonSmall);
		TextButton vertical = new TextButton("Doc", styles.outlineButtonSmall);
		horizontal.setChecked(true);
		horizontal.addListener(styles.clickListener(() -> setDirection(horizontal, vertical, Ship.HORIZONTAL)));
		vertical.addListener(styles.clickListener(() -> setDirection(horizontal, vertical, Ship.VERTICAl)));
		directionRow.add(horizontal).width(70).height(28).padRight(8);
		directionRow.add(vertical).width(70).height(28);
		sideInner.add(directionRow).left().padBottom(12).row();

		TextButton randomBtn = new TextButton("Dat ngau nhien", styles.outlineButtonSmall);
		randomBtn.addListener(styles.clickListener(this::autoPlaceShips));
		TextButton clearBtn = new TextButton("Xoa tat ca", styles.outlineButtonSmall);
		clearBtn.addListener(styles.clickListener(this::clearShips));
		Table sideButtons = new Table();
		sideButtons.add(randomBtn).width(110).height(28).padRight(6);
		sideButtons.add(clearBtn).width(90).height(28).padRight(6);
		sideButtons.add(back).width(70).height(28);
		sideInner.add(sideButtons).left();
		sideStack.add(sideBox);
		sideStack.add(sideInner);

		Table content = new Table();
		content.add(boardStack).padRight(18);
		content.add(sideStack);

		root.add(header).expandX().fillX().row();
		root.add(title).padBottom(12).row();
		root.add(content).padBottom(12).row();
		root.add(continueBtn).width(160).height(32).row();
		updateContinueState();
		stage.addActor(root);
	}

	private void selectShip(int index) {
		if (shipButtons[index].isDisabled()) {
			return;
		}
		selectedShipIndex = index;
		for (int i = 0; i < shipButtons.length; i++) {
			shipButtons[i].setChecked(i == index);
		}
	}

	private void setDirection(TextButton horizontal, TextButton vertical, int direction) {
		currentDirection = direction;
		horizontal.setChecked(direction == Ship.HORIZONTAL);
		vertical.setChecked(direction == Ship.VERTICAl);
	}

	private void tryPlaceShip(int row, int col) {
		if (selectedShipIndex < 0) {
			return;
		}
		int length = shipLengths[selectedShipIndex];
		if (!playerBoard.canPlaceShip(length, row, col, currentDirection)) {
			return;
		}
		Ship ship = new Ship(length);
		ship.setLocation(row, col);
		ship.setDirection(currentDirection);
		playerBoard.placeShip(ship);
		markShipPlaced(selectedShipIndex);
		setupBoard.loadFromBoard(playerBoard, true);
	}

	private void markShipPlaced(int index) {
		shipButtons[index].setText("[x] Tau " + shipLengths[index] + " o");
		shipButtons[index].setDisabled(true);
		shipButtons[index].setChecked(false);
		selectedShipIndex = -1;
		updateContinueState();
	}

	private void autoPlaceShips() {
		clearShips();
		playerBoard.setUpShip(playerBoard);
		for (int i = 0; i < shipButtons.length; i++) {
			markShipPlaced(i);
		}
		setupBoard.loadFromBoard(playerBoard, true);
		updateContinueState();
	}

	private void clearShips() {
		playerBoard.reset();
		setupBoard.loadFromBoard(playerBoard, true);
		selectedShipIndex = -1;
		for (int i = 0; i < shipButtons.length; i++) {
			shipButtons[i].setText("[ ] Tau " + shipLengths[i] + " o");
			shipButtons[i].setDisabled(false);
			shipButtons[i].setChecked(false);
		}
		updateContinueState();
	}

	private boolean allShipsPlaced() {
		for (TextButton button : shipButtons) {
			if (!button.isDisabled()) {
				return false;
			}
		}
		return true;
	}

	private void updateContinueState() {
		continueBtn.setDisabled(!allShipsPlaced());
	}

	@Override
	public void show() {
		super.show();
		setupBoard.loadFromBoard(playerBoard, true);
		updateContinueState();
	}

	@Override
	public void dispose() {
		setupBoard.dispose();
		boardBox.dispose();
		sideBox.dispose();
		super.dispose();
	}
}
