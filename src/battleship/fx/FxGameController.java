package battleship.fx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import battleship.ai.BattleshipAI;
import battleship.fx.components.BoardGrid;
import battleship.fx.screens.BattleScreen;
import battleship.fx.screens.SetupScreen;
import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class FxGameController {
	public interface GameEventListener {
		void onGameOver(boolean playerWon);
	}

    private final Board p1board;
    private final Board p2board;
    private final BoardGrid p1view;
    private final BoardGrid p2view;
    private final BoardGrid setupView;
    private final SetupScreen setupScreen;
    private final BattleScreen battleScreen;

    private int p1Hits = 0;
    private int p2Hits = 0;
    private final int winScore = 17;
    private boolean isGameOver = false;
    private boolean isBotThinking = false;
    private int aiLevel = BattleshipAI.HARD;
    private BattleshipAI botAI = new BattleshipAI();
    private List<Integer> aliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

    private List<Integer> remainingShips = new ArrayList<>();
    private int selectedLength = 5;
    private int direction = Ship.HORIZONTAL;
    private GameEventListener gameEventListener;

    public FxGameController(Board p1board, Board p2board, SetupScreen setupScreen, BattleScreen battleScreen) {
        this.p1board = p1board;
        this.p2board = p2board;
        this.setupScreen = setupScreen;
        this.battleScreen = battleScreen;
        this.setupView = setupScreen.getBoard();
        this.p1view = battleScreen.getPlayerBoard();
        this.p2view = battleScreen.getEnemyBoard();

        setupView.setOnCellClicked(this::handleSetupPlacement);
        setupView.setOnCellDoubleClicked(this::handleSetupDoubleClick);
        setupView.setOnCellDragDropped(this::handleSetupDragDropped);
        p2view.setOnCellClicked(this::handlePlayerFire);

        setupScreen.addListener(new SetupScreen.Listener() {
            @Override
            public void onBack() {
                // handled in Main
            }

            @Override
            public void onContinue() {
                // handled in Main
            }

            @Override
            public void onRotate() {
                direction = (direction == Ship.HORIZONTAL) ? Ship.VERTICAl : Ship.HORIZONTAL;
                setupScreen.setRotationLabel(direction == Ship.VERTICAl);
            }

            @Override
            public void onRandomize() {
                randomizeSetup();
            }

            @Override
            public void onReset() {
                resetSetup();
            }

            @Override
            public void onShipSelected(int length) {
                selectedLength = length;
                setupScreen.setShipsRemaining(remainingShips, selectedLength);
            }
        });

		resetSetup();
    }

    public void setDifficulty(int aiLevel) {
        this.aiLevel = aiLevel;
    }

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public void resetSetup() {
        p1board.reset();
        setupView.reset();
        remainingShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));
        selectedLength = remainingShips.get(0);
        direction = Ship.HORIZONTAL;
        setupScreen.setRotationLabel(false);
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(false);
        setupScreen.setStatusText("Place all 5 ships to continue.");
    }

    private void randomizeSetup() {
        resetSetup();
        randomPlaceShips(p1board);
        renderShips(p1board, setupView);
        remainingShips.clear();
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(true);
        setupScreen.setStatusText("Ships placed automatically. Ready to battle.");
    }

    public void startBattle() {
        if (!remainingShips.isEmpty()) {
            setupScreen.setStatusText("You must place all ships before starting.");
            return;
        }

        p2board.reset();
        randomPlaceShips(p2board);
        p1Hits = 0;
        p2Hits = 0;
        isGameOver = false;
        isBotThinking = false;
        botAI = new BattleshipAI();
        aliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

        p1view.reset();
        p2view.reset();
        renderShips(p1board, p1view);
        disableBoard(p1view);
        battleScreen.setEnemyEnabled(true);
        battleScreen.setTurnText("Your turn");
        battleScreen.setStatusText("Pick a target.");
    }

    private void handleSetupDragDropped(int row, int col, String data) {
        try {
            int length = Integer.parseInt(data);
            selectedLength = length;
            handleSetupPlacement(row, col);
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private void handleSetupDoubleClick(int row, int col) {
        if (p1board.getGrid()[row][col].getVal() == Node.SHIP) {
            Ship ship = p1board.getShipAt(row, col);
            if (ship != null) {
                p1board.removeShip(ship);
                // Erase from view
                for (int i = 0; i < ship.getLength(); i++) {
                    int r = ship.getRow() + (ship.getDirection() == Ship.VERTICAl ? i : 0);
                    int c = ship.getCol() + (ship.getDirection() == Ship.HORIZONTAL ? i : 0);
                    javafx.scene.control.Button cellBtn = setupView.getButton(r, c);
                    cellBtn.getStyleClass().removeAll("state-sea", "state-ship", "state-hit", "state-miss", "state-sunk");
                    cellBtn.getStyleClass().add("state-sea");
                }
                
                int newDir = ship.getDirection() == Ship.HORIZONTAL ? Ship.VERTICAl : Ship.HORIZONTAL;
                
                int offset = (ship.getDirection() == Ship.HORIZONTAL) ? (col - ship.getCol()) : (row - ship.getRow());
                int newRow = (newDir == Ship.VERTICAl) ? (row - offset) : row;
                int newCol = (newDir == Ship.HORIZONTAL) ? (col - offset) : col;
                
                if (p1board.canPlaceShip(ship.getLength(), newRow, newCol, newDir)) {
                    ship.setLocation(newRow, newCol);
                    ship.setDirection(newDir);
                } else {
                    setupScreen.setStatusText("Cannot rotate: Not enough space!");
                }
                
                p1board.placeShip(ship);
                renderShipCells(ship.getRow(), ship.getCol(), ship.getLength(), ship.getDirection(), setupView);
            }
        }
    }

    private void handleSetupPlacement(int row, int col) {
        if (remainingShips.isEmpty()) {
            return;
        }

        if (!p1board.canPlaceShip(selectedLength, row, col, direction)) {
            setupScreen.setStatusText("Invalid position. Try another cell or rotate.");
            return;
        }

        Ship ship = new Ship(selectedLength);
        ship.setLocation(row, col);
        ship.setDirection(direction);
        p1board.placeShip(ship);
        renderShipCells(row, col, selectedLength, direction, setupView);

        remainingShips.remove(Integer.valueOf(selectedLength));
        if (!remainingShips.contains(selectedLength) && !remainingShips.isEmpty()) {
            selectedLength = remainingShips.get(0);
        }
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(remainingShips.isEmpty());
        if (remainingShips.isEmpty()) {
            setupScreen.setStatusText("All ships placed. Press Continue.");
        } else {
            setupScreen.setStatusText("Ship placed. Remaining ships: " + remainingShips);
        }
    }

    private void handlePlayerFire(int row, int col) {
        if (isGameOver || isBotThinking) {
            return;
        }

        Node targetNode = p2board.getGrid()[row][col];
        if (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS) {
            return;
        }

        boolean isHit = p2board.fireAt(row, col);
        p2view.updateButtonState(row, col, isHit);

        if (isHit) {
            p1Hits++;
            if (p2board.isSunkAt(row, col)) {
                markSunkShip(p2board, p2view, row, col);
                battleScreen.setStatusText("Hit! Enemy ship sunk.");
            } else {
                battleScreen.setStatusText("Hit!");
            }
            battleScreen.setTurnText("Your turn");
            battleScreen.setEnemyEnabled(true);
            checkWinCondition();
        } else if (!isGameOver) {
			battleScreen.setTurnText("Enemy turn");
			battleScreen.setStatusText("Miss. Enemy is thinking...");
			battleScreen.setEnemyEnabled(false);
			delayedBotFire();
        }
    }

    private void delayedBotFire() {
        isBotThinking = true;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            switch (aiLevel) {
                case BattleshipAI.EASY:
                    botFireEasy();
                    break;
                case BattleshipAI.MEDIUM:
                    botFireMedium();
                    break;
                default:
                    botFireHard();
                    break;
            }
        });
        pause.play();
    }

    private void botFireEasy() {
        if (isGameOver) {
            isBotThinking = false;
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.EASY, aliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            battleScreen.setStatusText("Enemy hit your ship.");
            checkWinCondition();
            botFireEasy();
        } else {
            battleScreen.setTurnText("Your turn");
            battleScreen.setStatusText("Enemy missed.");
            battleScreen.setEnemyEnabled(true);
            isBotThinking = false;
        }
    }

    private void botFireMedium() {
        if (isGameOver) {
            isBotThinking = false;
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.MEDIUM, aliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            battleScreen.setStatusText("Enemy hit your ship.");
            checkWinCondition();
            botAI.updateAfterFire(p1board, targetNode, isHit);
            if (!isGameOver) {
                delayedBotFire();
            } else {
                isBotThinking = false;
            }
        } else {
            battleScreen.setTurnText("Your turn");
            battleScreen.setStatusText("Enemy missed.");
            battleScreen.setEnemyEnabled(true);
            isBotThinking = false;
        }
    }

    private void botFireHard() {
        if (isGameOver) {
            isBotThinking = false;
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.HARD, aliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        Node[][] p1Grid = p1board.getGrid();

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            battleScreen.setStatusText("Enemy hit your ship.");
            checkWinCondition();

            botAI.updateAfterFire(p1board, targetNode, isHit);

            if (p1board.isSunkAt(r, c)) {
                int lengthShip = p1board.lengthShipIs(r, c);
                aliveShips.remove(Integer.valueOf(lengthShip));
                p1board.markShipAsSunk(r, c, p1Grid);
				battleScreen.setStatusText("Enemy sunk your ship.");

                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (p1board.getGrid()[i][j].getVal() == Node.SUNK) {
                            p1view.markButtonAsSunk(i, j);
                        }
                    }
                }
            }

            if (!isGameOver) {
                delayedBotFire();
            } else {
                isBotThinking = false;
            }
        } else {
            battleScreen.setTurnText("Your turn");
            battleScreen.setStatusText("Enemy missed.");
            battleScreen.setEnemyEnabled(true);
            isBotThinking = false;
        }
    }

    private void checkWinCondition() {
        if (p1Hits == winScore) {
            isGameOver = true;
            isBotThinking = false;
            battleScreen.setTurnText("Game over");
            battleScreen.setStatusText("You win!");
            battleScreen.setEnemyEnabled(false);
            if (gameEventListener != null) {
                gameEventListener.onGameOver(true);
            }
        } else if (p2Hits == winScore) {
            isGameOver = true;
            isBotThinking = false;
            battleScreen.setTurnText("Game over");
            battleScreen.setStatusText("You lose.");
            battleScreen.setEnemyEnabled(false);
            if (gameEventListener != null) {
                gameEventListener.onGameOver(false);
            }
        }
    }

    private void renderShips(Board board, BoardGrid grid) {
        Node[][] nodes = board.getGrid();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (nodes[r][c].getVal() == Node.SHIP) {
                    grid.showShipCell(r, c);
                }
            }
        }
    }

    private void renderShipCells(int row, int col, int length, int dir, BoardGrid grid) {
        for (int i = 0; i < length; i++) {
            int r = row + (dir == Ship.VERTICAl ? i : 0);
            int c = col + (dir == Ship.HORIZONTAL ? i : 0);
            grid.showShipCell(r, c);
        }
    }

    private void disableBoard(BoardGrid grid) {
        for (int r = 0; r < BoardGrid.SIZE; r++) {
            for (int c = 0; c < BoardGrid.SIZE; c++) {
                grid.setCellEnabled(r, c, false);
            }
        }
    }

    private void markSunkShip(Board board, BoardGrid grid, int row, int col) {
        Node[][] nodes = board.getGrid();
        board.markShipAsSunk(row, col, nodes);
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (nodes[r][c].getVal() == Node.SUNK) {
                    grid.markButtonAsSunk(r, c);
                }
            }
        }
    }

    private void randomPlaceShips(Board board) {
        Random rd = new Random();
        int[] lengths = { 5, 4, 3, 3, 2 };

        for (int len : lengths) {
            boolean placed = false;
            while (!placed) {
                int r = rd.nextInt(10);
                int c = rd.nextInt(10);
                int dir = rd.nextInt(2);

                if (board.canPlaceShip(len, r, c, dir)) {
                    Ship s = new Ship(len);
                    s.setLocation(r, c);
                    s.setDirection(dir);
                    board.placeShip(s);
                    placed = true;
                }
            }
        }
    }
}
