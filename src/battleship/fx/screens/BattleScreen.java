package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import battleship.fx.components.BoardGrid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

public class BattleScreen extends BorderPane {
    public interface Listener {
        void onOpenSettings();
    }

    private Listener listener;
    private final BoardGrid playerBoard = new BoardGrid();
    private final BoardGrid enemyBoard = new BoardGrid();
    
    private final Label turnLabel = new Label();
    private final Label statusLabel = new Label();
    
    // Fleet Status
    private final HBox playerFleetStatus = new HBox(5);
    private final HBox enemyFleetStatus = new HBox(5);
    
    // Battle Log
    private final VBox logContent = new VBox(2);
    private final ScrollPane logScrollPane = new ScrollPane(logContent);

    public BattleScreen() {
        getStyleClass().add("screen-root");
        setPadding(new Insets(10, 20, 10, 20));

        // TOP: Title
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("COMBAT ZONE");
        titleLabel.getStyleClass().add("screen-title");
        
        topBar.getChildren().add(titleLabel);
        setTop(topBar);

        // CENTER: Boards
        HBox centerBox = new HBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10, 0, 10, 0));

        // Player side
        VBox playerSide = new VBox(8);
        playerSide.setAlignment(Pos.CENTER);
        Label pLabel = new Label("YOUR FLEET");
        pLabel.getStyleClass().add("board-title");
        playerFleetStatus.setAlignment(Pos.CENTER);
        playerBoard.getStyleClass().addAll("board-container", "player-board");
        playerSide.getChildren().addAll(pLabel, playerFleetStatus, playerBoard);

        // Enemy side
        VBox enemySide = new VBox(8);
        enemySide.setAlignment(Pos.CENTER);
        Label eLabel = new Label("ENEMY SECTOR");
        eLabel.getStyleClass().add("board-title");
        enemyFleetStatus.setAlignment(Pos.CENTER);
        enemyBoard.getStyleClass().addAll("board-container", "enemy-board");
        enemySide.getChildren().addAll(eLabel, enemyFleetStatus, enemyBoard);

        centerBox.getChildren().addAll(playerSide, enemySide);
        setCenter(centerBox);

        // RIGHT: Battle Log
        VBox rightSide = new VBox(10);
        rightSide.setPrefWidth(200);
        rightSide.setPadding(new Insets(0, 0, 0, 10));
        Label logTitle = new Label("MISSION LOG");
        logTitle.getStyleClass().add("board-title");
        
        logContent.setPadding(new Insets(5));
        logScrollPane.setFitToWidth(true);
        logScrollPane.setPrefHeight(400);
        logScrollPane.getStyleClass().add("log-container");
        VBox.setVgrow(logScrollPane, Priority.ALWAYS);
        
        rightSide.getChildren().addAll(logTitle, logScrollPane);
        setRight(rightSide);

        // BOTTOM: Turn and Status
        VBox bottomBar = new VBox(5);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        turnLabel.getStyleClass().add("status-emphasis");
        statusLabel.getStyleClass().add("info-label");
        bottomBar.getChildren().addAll(turnLabel, statusLabel);
        setBottom(bottomBar);

        initFleetStatus(playerFleetStatus);
        initFleetStatus(enemyFleetStatus);
        updateLanguage();
    }

    private void initFleetStatus(HBox container) {
        container.getChildren().clear();
        int[] shipLengths = {5, 4, 3, 3, 2};
        for (int len : shipLengths) {
            HBox shipIndicator = new HBox(1);
            shipIndicator.getStyleClass().add("fleet-indicator-ship");
            for (int i = 0; i < len; i++) {
                Label segment = new Label();
                segment.setPrefSize(8, 12);
                segment.getStyleClass().add("fleet-segment-alive");
                shipIndicator.getChildren().add(segment);
            }
            container.getChildren().add(shipIndicator);
        }
    }

    public void updateFleetStatus(boolean isPlayer, List<Integer> aliveShipLengths) {
        HBox container = isPlayer ? playerFleetStatus : enemyFleetStatus;
        container.getChildren().clear();
        
        int[] allLengths = {5, 4, 3, 3, 2};
        List<Integer> tempAlive = new ArrayList<>(aliveShipLengths);
        
        for (int len : allLengths) {
            HBox shipIndicator = new HBox(1);
            boolean found = false;
            for (int i = 0; i < tempAlive.size(); i++) {
                if (tempAlive.get(i) == len) {
                    tempAlive.remove(i);
                    found = true;
                    break;
                }
            }
            
            for (int i = 0; i < len; i++) {
                Label segment = new Label();
                segment.setPrefSize(6, 10);
                segment.getStyleClass().add(found ? "fleet-segment-alive" : "fleet-segment-sunk");
                shipIndicator.getChildren().add(segment);
            }
            container.getChildren().add(shipIndicator);
        }
    }

    public void addLogEvent(String message, boolean highlight) {
        Label entry = new Label("> " + message);
        entry.setWrapText(true);
        entry.getStyleClass().add(highlight ? "log-entry-highlight" : "log-entry");
        logContent.getChildren().add(0, entry); // Add to top
    }

    public void clearLog() {
        logContent.getChildren().clear();
    }

    public void updateLanguage() {
        turnLabel.setText(LocalizationManager.get("your_turn"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public BoardGrid getPlayerBoard() {
        return playerBoard;
    }

    public BoardGrid getEnemyBoard() {
        return enemyBoard;
    }

    public void updateTurnDisplay(boolean isPlayerTurn) {
        playerBoard.getStyleClass().remove("active-board");
        enemyBoard.getStyleClass().remove("active-board");
        
        if (isPlayerTurn) {
            // Player's turn: highlight the target (enemy board)
            enemyBoard.getStyleClass().add("active-board");
        } else {
            // Enemy's turn: highlight the target (player board)
            playerBoard.getStyleClass().add("active-board");
        }
    }

    public void setTurnText(String text) {
        turnLabel.setText(text);
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public void setEnemyEnabled(boolean enabled) {
        enemyBoard.setDisable(!enabled);
    }
}
