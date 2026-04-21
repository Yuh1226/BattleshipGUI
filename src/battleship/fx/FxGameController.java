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
		void onGameOver(boolean playerWon, int shots, int hits, int sunk, String duration, String difficulty);
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

    // Timer logic
    private javafx.animation.Timeline turnTimeline;
    private int secondsRemaining = 30;
    private static final int MAX_TURN_TIME = 30;
    private long matchStartTime;

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
        setupView.setOnCellDragDetected(this::handleSetupDragDetected);
        setupView.setOnCellDragDone(this::handleSetupDragDone);
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
        setupScreen.setStatusText(LocalizationManager.get("status_place_all"));
    }

    private void randomizeSetup() {
        resetSetup();
        randomPlaceShips(p1board);
        renderShips(p1board, setupView);
        remainingShips.clear();
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(true);
        setupScreen.setStatusText(LocalizationManager.get("status_auto_done"));
    }

    public void startBattle() {
        if (!remainingShips.isEmpty()) {
            setupScreen.setStatusText(LocalizationManager.get("status_need_all"));
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
        battleScreen.addLogEvent(LocalizationManager.get("log_battle_started"), true);
        battleScreen.updateFleetStatus(true, p1AliveShips);
        battleScreen.updateFleetStatus(false, p2AliveShips);
        renderShips(p1board, p1view);
        disableBoard(p1view);
        battleScreen.setEnemyEnabled(true);
        battleScreen.updateTurnDisplay(true); // Player turn starts
        battleScreen.setTurnText(LocalizationManager.get("turn_player"));
        battleScreen.setStatusText(LocalizationManager.get("status_pick_target"));
        matchStartTime = System.currentTimeMillis();
        startTurnTimer();
    }

    private void startTurnTimer() {
        stopTurnTimer();
        secondsRemaining = MAX_TURN_TIME;
        battleScreen.updateTimer(1.0, secondsRemaining);
        battleScreen.setTimerVisible(true);

        turnTimeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.seconds(1), e -> {
                secondsRemaining--;
                double progress = (double) secondsRemaining / MAX_TURN_TIME;
                battleScreen.updateTimer(progress, secondsRemaining);
                if (secondsRemaining <= 0) {
                    handleTimeOut();
                }
            })
        );
        turnTimeline.setCycleCount(MAX_TURN_TIME);
        turnTimeline.play();
    }

    private void stopTurnTimer() {
        if (turnTimeline != null) {
            turnTimeline.stop();
            turnTimeline = null;
        }
        battleScreen.setTimerVisible(false);
    }

    private void handleTimeOut() {
        stopTurnTimer();
        if (isGameOver || isBotThinking) return;

        battleScreen.addLogEvent("TIME OUT!", true);
        // Random shot for player
        Random rd = new Random();
        int r, c;
        do {
            r = rd.nextInt(10);
            c = rd.nextInt(10);
        } while (p2board.getGrid()[r][c].getVal() == Node.HIT || p2board.getGrid()[r][c].getVal() == Node.MISS);
        
        handlePlayerFire(r, c);
    }

    private void handleSetupDragDetected(int row, int col) {
        Ship ship = p1board.getShipAt(row, col);
        if (ship != null) {
            // Initiate move logic
            javafx.scene.input.Dragboard db = setupView.getButton(row, col).startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(String.valueOf(ship.getLength()));
            db.setContent(content);
            
            // Set moving ship info
            this.direction = ship.getDirection();
            this.selectedLength = ship.getLength();
            
            // Remove ship temporarily to allow previewing new position
            p1board.removeShip(ship);
            setupView.reset();
            renderShips(p1board, setupView);
            
            setupScreen.setStatusText(String.format(LocalizationManager.get("status_moving"), ship.getLength()));
        }
    }

    private void handleSetupDragDone(int row, int col, boolean success) {
        if (!success) {
            // If drop failed (e.g. dropped outside grid), the ship is already removed from board.
            // We must add it back to remaining ships so it doesn't disappear.
            if (!remainingShips.contains(selectedLength)) {
                remainingShips.add(selectedLength);
                remainingShips.sort((a, b) -> b - a);
                setupScreen.setShipsRemaining(remainingShips, remainingShips.get(0));
                setupScreen.setStatusText(LocalizationManager.get("status_move_cancelled"));
            }
        }
        setupView.reset();
        renderShips(p1board, setupView);
    }

    private void handleSetupDragDropped(int row, int col, String data) {
        try {
            int length = Integer.parseInt(data);
            
            if (p1board.canPlaceShip(length, row, col, direction)) {
                Ship ship = new Ship(length);
                ship.setLocation(row, col);
                ship.setDirection(direction);
                p1board.placeShip(ship);
                setupScreen.setStatusText(LocalizationManager.get("status_move_success"));
                
                // If this was a "move" from the board, it was already removed in DragDetected.
                // If it was from the dock:
                remainingShips.remove(Integer.valueOf(length));
            } else {
                if (!remainingShips.contains(length)) {
                    remainingShips.add(length);
                    remainingShips.sort((a, b) -> b - a);
                }
                setupScreen.setStatusText(LocalizationManager.get("status_invalid_pos"));
            }
            
            setupView.reset();
            renderShips(p1board, setupView);
            
            if (!remainingShips.isEmpty()) {
                selectedLength = remainingShips.get(0);
            }
            setupScreen.setShipsRemaining(remainingShips, selectedLength);
            setupScreen.setContinueEnabled(remainingShips.isEmpty());
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
                renderShipCells(ship.getRow(), ship.getCol(), ship.getLength(), ship.getDirection(), setupView, true);
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
        renderShipCells(row, col, selectedLength, direction, setupView, true); // Animate new placement

        remainingShips.remove(Integer.valueOf(selectedLength));
        if (!remainingShips.isEmpty()) {
            selectedLength = remainingShips.get(0);
        }
        setupScreen.setShipsRemaining(remainingShips, selectedLength);
        setupScreen.setContinueEnabled(remainingShips.isEmpty());
        if (remainingShips.isEmpty()) {
            setupScreen.setStatusText(LocalizationManager.get("status_all_placed"));
        } else {
            setupScreen.setStatusText(String.format(LocalizationManager.get("status_next_ship"), selectedLength));
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

        stopTurnTimer();
        p1Shots++;
        String coord = (char)('A' + col) + "" + (row + 1);
        
        // Sound: Fire!
        AudioManager.getInstance().playSound("fire");
        
        // Delay slightly for the projectile to "travel"
        PauseTransition travelDelay = new PauseTransition(Duration.millis(800));
        travelDelay.setOnFinished(e -> {
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
                    battleScreen.addLogEvent(String.format(LocalizationManager.get("log_player_sunk"), len, coord), true);
                    
                    markSunkShip(p2board, p2view, row, col);
                    battleScreen.setStatusText(LocalizationManager.get("status_hit_sunk"));
                    
                    // Prioritize 'sunk' sound over 'hit' sound
                    AudioManager.getInstance().playSound("sunk");
                } else {
                    battleScreen.addLogEvent(String.format(LocalizationManager.get("log_player_hit"), coord), false);
                    battleScreen.setStatusText(LocalizationManager.get("status_hit"));
                    AudioManager.getInstance().playSound("hit");
                }
                battleScreen.updateTurnDisplay(true);
                battleScreen.setTurnText(LocalizationManager.get("turn_player"));
                battleScreen.setEnemyEnabled(true);
                if (!checkWinCondition()) {
                    startTurnTimer();
                }
            } else {
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_player_miss"), coord), false);
                battleScreen.updateTurnDisplay(false);
                battleScreen.setTurnText(LocalizationManager.get("turn_enemy"));
                battleScreen.setStatusText(LocalizationManager.get("status_miss_thinking"));
                battleScreen.setEnemyEnabled(false);
                AudioManager.getInstance().playSound("miss");
                delayedBotFire();
            }
        });
        travelDelay.play();
    }

    private void delayedBotFire() {
        stopTurnTimer();
        isBotThinking = true;
        battleScreen.updateTurnDisplay(false); // Highlight target (player board)
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
            stopTurnTimer();
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.EASY, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        // Sound: Fire!
        AudioManager.getInstance().playSound("fire");

        PauseTransition travelDelay = new PauseTransition(Duration.millis(800));
        travelDelay.setOnFinished(e -> {
            boolean isHit = p1board.fireAt(r, c);
            p1view.updateButtonState(r, c, isHit);

            if (isHit) {
                p2Hits++;
                p1view.shake();
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_hit"), coord), false);
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_hit"));
                AudioManager.getInstance().playSound("hit");
                
                if (p1board.isSunkAt(r, c)) {
                    int len = p1board.lengthShipIs(r, c);
                    p1AliveShips.remove(Integer.valueOf(len));
                    battleScreen.updateFleetStatus(true, p1AliveShips);
                    battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_sunk"), len), true);
                    p1view.shake();
                    markSunkShip(p1board, p1view, r, c);
                    AudioManager.getInstance().playSound("sunk");
                }
                
                if (!checkWinCondition()) {
                    botFireEasy();
                }
            } else {
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_miss"), coord), false);
                battleScreen.updateTurnDisplay(true);
                battleScreen.setTurnText(LocalizationManager.get("turn_player"));
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_miss"));
                battleScreen.setEnemyEnabled(true);
                isBotThinking = false;
                AudioManager.getInstance().playSound("miss");
                startTurnTimer();
            }
        });
        travelDelay.play();
    }

    private void botFireMedium() {
        if (isGameOver) {
            isBotThinking = false;
            stopTurnTimer();
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.MEDIUM, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        AudioManager.getInstance().playSound("fire");

        PauseTransition travelDelay = new PauseTransition(Duration.millis(800));
        travelDelay.setOnFinished(e -> {
            boolean isHit = p1board.fireAt(r, c);
            p1view.updateButtonState(r, c, isHit);

            if (isHit) {
                p2Hits++;
                p1view.shake();
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_hit"), coord), false);
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_hit"));
                AudioManager.getInstance().playSound("hit");
                botAI.updateAfterFire(p1board, targetNode, isHit);
                
                if (p1board.isSunkAt(r, c)) {
                    int len = p1board.lengthShipIs(r, c);
                    p1AliveShips.remove(Integer.valueOf(len));
                    battleScreen.updateFleetStatus(true, p1AliveShips);
                    battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_sunk"), len), true);
                    p1view.shake();
                    markSunkShip(p1board, p1view, r, c);
                    AudioManager.getInstance().playSound("sunk");
                }

                if (!checkWinCondition()) {
                    delayedBotFire();
                } else {
                    isBotThinking = false;
                }
            } else {
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_miss"), coord), false);
                battleScreen.updateTurnDisplay(true);
                battleScreen.setTurnText(LocalizationManager.get("turn_player"));
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_miss"));
                battleScreen.setEnemyEnabled(true);
                isBotThinking = false;
                AudioManager.getInstance().playSound("miss");
                startTurnTimer();
            }
        });
        travelDelay.play();
    }

    private void botFireHard() {
        if (isGameOver) {
            isBotThinking = false;
            stopTurnTimer();
            return;
        }

        Node targetNode = botAI.determineTarget(p1board, BattleshipAI.HARD, p1AliveShips);
        int r = targetNode.getX();
        int c = targetNode.getY();
        String coord = (char)('A' + c) + "" + (r + 1);

        AudioManager.getInstance().playSound("fire");

        PauseTransition travelDelay = new PauseTransition(Duration.millis(800));
        travelDelay.setOnFinished(e -> {
            boolean isHit = p1board.fireAt(r, c);
            p1view.updateButtonState(r, c, isHit);

            if (isHit) {
                p2Hits++;
                p1view.shake();
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_hit"), coord), false);
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_hit"));
                AudioManager.getInstance().playSound("hit");

                botAI.updateAfterFire(p1board, targetNode, isHit);

                if (p1board.isSunkAt(r, c)) {
                    int len = p1board.lengthShipIs(r, c);
                    p1AliveShips.remove(Integer.valueOf(len));
                    battleScreen.updateFleetStatus(true, p1AliveShips);
                    battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_sunk"), len), true);
                    p1view.shake();
                    markSunkShip(p1board, p1view, r, c);
                    AudioManager.getInstance().playSound("sunk");
                }

                if (!checkWinCondition()) {
                    delayedBotFire();
                } else {
                    isBotThinking = false;
                }
            } else {
                battleScreen.addLogEvent(String.format(LocalizationManager.get("log_enemy_miss"), coord), false);
                battleScreen.updateTurnDisplay(true);
                battleScreen.setTurnText(LocalizationManager.get("turn_player"));
                battleScreen.setStatusText(LocalizationManager.get("status_enemy_miss"));
                battleScreen.setEnemyEnabled(true);
                isBotThinking = false;
                AudioManager.getInstance().playSound("miss");
                startTurnTimer();
            }
        });
        travelDelay.play();
    }

    private boolean checkWinCondition() {
        if (p1Hits == winScore || p2Hits == winScore) {
            isGameOver = true;
            isBotThinking = false;
            stopTurnTimer();
            
            long durationMillis = System.currentTimeMillis() - matchStartTime;
            long mins = (durationMillis / 1000) / 60;
            long secs = (durationMillis / 1000) % 60;
            String durationStr = String.format("%02d:%02d", mins, secs);
            
            String diffStr = LocalizationManager.get("hard");
            if (aiLevel == BattleshipAI.EASY) diffStr = LocalizationManager.get("easy");
            else if (aiLevel == BattleshipAI.MEDIUM) diffStr = LocalizationManager.get("normal");

            boolean playerWon = (p1Hits == winScore);
            battleScreen.setTurnText(LocalizationManager.get("turn_game_over"));
            battleScreen.setStatusText(playerWon ? LocalizationManager.get("win") : LocalizationManager.get("lose"));
            battleScreen.setEnemyEnabled(false);
            
            if (gameEventListener != null) {
                gameEventListener.onGameOver(playerWon, p1Shots, p1Hits, p1Sunk, durationStr, diffStr);
            }
            return true;
        }
        return false;
    }

    private void renderShips(Board board, BoardGrid grid) {
        Node[][] nodes = board.getGrid();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (nodes[r][c].getVal() == Node.SHIP) {
                    grid.showShipCell(r, c, false); // No animation for full render
                }
            }
        }
    }

    private void renderShipCells(int row, int col, int length, int dir, BoardGrid grid, boolean animate) {
        for (int i = 0; i < length; i++) {
            int r = row + (dir == Ship.VERTICAl ? i : 0);
            int c = col + (dir == Ship.HORIZONTAL ? i : 0);
            grid.showShipCell(r, c, animate);
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
        Ship ship = board.getShipAt(row, col);
        if (ship != null) {
            Node[][] nodes = board.getGrid();
            board.markShipAsSunk(row, col, nodes);
            
            for (int i = 0; i < ship.getLength(); i++) {
                int r = ship.getRow() + (ship.getDirection() == Ship.VERTICAl ? i : 0);
                int c = ship.getCol() + (ship.getDirection() == Ship.HORIZONTAL ? i : 0);
                grid.markButtonAsSunk(r, c);
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
