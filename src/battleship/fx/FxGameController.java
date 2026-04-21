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
		void onGameOver(boolean playerWon, int shots, int hits, int sunk);
	}

    private final Board p1board;
    private final Board p2board;
    private final BoardGrid p1view;
    private final BoardGrid p2view;
    private final BoardGrid setupView;
    private final SetupScreen setupScreen;
    private final BattleScreen battleScreen;

    private int p1Hits = 0;
    private int p1Shots = 0;
    private int p1Sunk = 0;
    private int p2Hits = 0;
    private final int winScore = 17;
    private boolean isGameOver = false;
    private boolean isBotThinking = false;
    private int aiLevel = BattleshipAI.HARD;
    private BattleshipAI botAI = new BattleshipAI();
    private List<Integer> p1AliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));
    private List<Integer> p2AliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

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

        setupView.setDirectionProvider(() -> direction);
        setupView.setPlacementValidator(p1board::canPlaceShip);

        setupView.setOnCellClicked(this::handleSetupPlacement);
        setupView.setOnCellRightClicked(this::handleSetupRemoveShip);
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

            @Override
            public void onOpenSettings() {
                // handled in Main
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
        p1Shots = 0;
        p1Sunk = 0;
        p2Hits = 0;
        isGameOver = false;
        isBotThinking = false;
        botAI = new BattleshipAI();
        p1AliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));
        p2AliveShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

        p1view.reset();
        p2view.reset();
        battleScreen.clearLog();
        battleScreen.addLogEvent("Battle started!", true);
        battleScreen.updateFleetStatus(true, p1AliveShips);
        battleScreen.updateFleetStatus(false, p2AliveShips);
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

    private void handleSetupRemoveShip(int row, int col) {
        Ship ship = p1board.getShipAt(row, col);
        if (ship != null) {
            p1board.removeShip(ship);
            remainingShips.add(ship.getLength());
            remainingShips.sort((a, b) -> b - a);
            selectedLength = remainingShips.get(0);
            
            setupView.reset();
            renderShips(p1board, setupView);
            setupScreen.setShipsRemaining(remainingShips, selectedLength);
            setupScreen.setContinueEnabled(false);
            setupScreen.setStatusText("Ship removed and returned to fleet.");
        }
    }

    private void handleSetupPlacement(int row, int col) {
        // If clicking on an existing ship, rotate it
        Ship existingShip = p1board.getShipAt(row, col);
        if (existingShip != null) {
            // Store old values in case rotation fails
            int oldRow = existingShip.getRow();
            int oldCol = existingShip.getCol();
            int oldDir = existingShip.getDirection();
            
            p1board.removeShip(existingShip);
            
            int newDir = (oldDir == Ship.HORIZONTAL) ? Ship.VERTICAl : Ship.HORIZONTAL;
            // Pivot logic: find index of clicked cell within the ship
            int offset = (oldDir == Ship.HORIZONTAL) ? (col - oldCol) : (row - oldRow);
            int newRow = (newDir == Ship.VERTICAl) ? (row - offset) : row;
            int newCol = (newDir == Ship.HORIZONTAL) ? (col - offset) : col;
            
            if (p1board.canPlaceShip(existingShip.getLength(), newRow, newCol, newDir)) {
                existingShip.setLocation(newRow, newCol);
                existingShip.setDirection(newDir);
                setupScreen.setStatusText("Ship rotated.");
            } else {
                // Restore if cannot rotate
                existingShip.setLocation(oldRow, oldCol);
                existingShip.setDirection(oldDir);
                setupScreen.setStatusText("Cannot rotate: Collision or out of bounds.");
            }
            
            p1board.placeShip(existingShip);
            setupView.reset();
            renderShips(p1board, setupView);
            return;
        }

        if (remainingShips.isEmpty()) {
            return;
        }

        if (!p1board.canPlaceShip(selectedLength, row, col, direction)) {
            setupScreen.setStatusText("Invalid position: Ships must not touch each other.");
            return;
        }

        Ship ship = new Ship(selectedLength);
        ship.setLocation(row, col);
        ship.setDirection(direction);
        p1board.placeShip(ship);
        renderShipCells(row, col, selectedLength, direction, setupView);

        remainingShips.remove(Integer.valueOf(selectedLength));
        if (!remainingShips.isEmpty()) {
            selectedLength = remainingShips.get(0);
        }
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(remainingShips.isEmpty());
        if (remainingShips.isEmpty()) {
            setupScreen.setStatusText("All ships placed. Press Continue.");
        } else {
            setupScreen.setStatusText("Ship placed. Next ship: " + selectedLength);
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

        p1Shots++;
        String coord = (char)('A' + col) + "" + (row + 1);
        boolean isHit = p2board.fireAt(row, col);
        p2view.updateButtonState(row, col, isHit);

        if (isHit) {
            p1Hits++;
            p2view.shake();
            if (p2board.isSunkAt(row, col)) {
                p1Sunk++;
                int len = p2board.lengthShipIs(row, col);
                p2AliveShips.remove(Integer.valueOf(len));
                battleScreen.updateFleetStatus(false, p2AliveShips);
                battleScreen.addLogEvent("YOU SUNK A SHIP (" + len + ") at " + coord, true);
                
                markSunkShip(p2board, p2view, row, col);
                battleScreen.setStatusText("Hit! Enemy ship sunk.");
            } else {
                battleScreen.addLogEvent("Player hit at " + coord, false);
                battleScreen.setStatusText("Hit!");
            }
            battleScreen.setTurnText("Your turn");
            battleScreen.setEnemyEnabled(true);
            checkWinCondition();
        } else if (!isGameOver) {
            battleScreen.addLogEvent("Player missed at " + coord, false);
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

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.EASY, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            p1view.shake();
            battleScreen.addLogEvent("Enemy hit your ship at " + coord, false);
            battleScreen.setStatusText("Enemy hit your ship.");
            
            if (p1board.isSunkAt(r, c)) {
                int len = p1board.lengthShipIs(r, c);
                p1AliveShips.remove(Integer.valueOf(len));
                battleScreen.updateFleetStatus(true, p1AliveShips);
                battleScreen.addLogEvent("ENEMY SUNK YOUR SHIP (" + len + ")!", true);
                p1view.shake();
                markSunkShip(p1board, p1view, r, c);
            }
            
            checkWinCondition();
            botFireEasy();
        } else {
            battleScreen.addLogEvent("Enemy missed at " + coord, false);
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

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.MEDIUM, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            p1view.shake();
            battleScreen.addLogEvent("Enemy hit your ship at " + coord, false);
            battleScreen.setStatusText("Enemy hit your ship.");
            botAI.updateAfterFire(p1board, targetNode, isHit);
            
            if (p1board.isSunkAt(r, c)) {
                int len = p1board.lengthShipIs(r, c);
                p1AliveShips.remove(Integer.valueOf(len));
                battleScreen.updateFleetStatus(true, p1AliveShips);
                battleScreen.addLogEvent("ENEMY SUNK YOUR SHIP (" + len + ")!", true);
                p1view.shake();
                markSunkShip(p1board, p1view, r, c);
            }

            if (!isGameOver) {
                checkWinCondition();
                delayedBotFire();
            } else {
                isBotThinking = false;
            }
        } else {
            battleScreen.addLogEvent("Enemy missed at " + coord, false);
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

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.HARD, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        boolean isHit = p1board.fireAt(r, c);
        p1view.updateButtonState(r, c, isHit);

        if (isHit) {
            p2Hits++;
            p1view.shake();
            battleScreen.addLogEvent("Enemy hit your ship at " + coord, false);
            battleScreen.setStatusText("Enemy hit your ship.");
            checkWinCondition();

            botAI.updateAfterFire(p1board, targetNode, isHit);

            if (p1board.isSunkAt(r, c)) {
                int len = p1board.lengthShipIs(r, c);
                p1AliveShips.remove(Integer.valueOf(len));
                battleScreen.updateFleetStatus(true, p1AliveShips);
                battleScreen.addLogEvent("ENEMY SUNK YOUR SHIP (" + len + ")!", true);
                p1view.shake();
                markSunkShip(p1board, p1view, r, c);
            }

            if (!isGameOver) {
                delayedBotFire();
            } else {
                isBotThinking = false;
            }
        } else {
            battleScreen.addLogEvent("Enemy missed at " + coord, false);
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
                gameEventListener.onGameOver(true, p1Shots, p1Hits, p1Sunk);
            }
        } else if (p2Hits == winScore) {
            isGameOver = true;
            isBotThinking = false;
            battleScreen.setTurnText("Game over");
            battleScreen.setStatusText("You lose.");
            battleScreen.setEnemyEnabled(false);
            if (gameEventListener != null) {
                gameEventListener.onGameOver(false, p1Shots, p1Hits, p1Sunk);
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
