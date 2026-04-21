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
    private final Label timerLabel = new Label("30s");
    private final javafx.scene.control.ProgressBar timerProgress = new javafx.scene.control.ProgressBar(1.0);
    private javafx.animation.ScaleTransition pulseTransition;
    
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
        VBox bottomBar = new VBox(8);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        
        turnLabel.getStyleClass().add("status-emphasis");
        statusLabel.getStyleClass().add("info-label");
        
        timerProgress.setPrefWidth(300);
        timerProgress.getStyleClass().add("timer-bar");
        timerLabel.getStyleClass().add("info-label");
        timerLabel.setStyle("-fx-font-weight: bold;");
        
        VBox timerBox = new VBox(2, timerProgress, timerLabel);
        timerBox.setAlignment(Pos.CENTER);
        
        bottomBar.getChildren().addAll(turnLabel, timerBox, statusLabel);
        setBottom(bottomBar);

        // Initialize Pulse Animation
        pulseTransition = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(800), turnLabel);
        pulseTransition.setFromX(1.0); pulseTransition.setFromY(1.0);
        pulseTransition.setToX(1.1); pulseTransition.setToY(1.1);
        pulseTransition.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulseTransition.setAutoReverse(true);

        initFleetStatus(playerFleetStatus);
        initFleetStatus(enemyFleetStatus);
        updateLanguage();
    }

    public void updateTimer(double progress, int secondsRemaining) {
        timerProgress.setProgress(progress);
        timerLabel.setText(secondsRemaining + "s");
        
        if (secondsRemaining <= 5) {
            timerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #fa3e3e;");
            // Flash effect for critical time
            if (secondsRemaining % 2 == 0) {
                timerLabel.setOpacity(0.3);
            } else {
                timerLabel.setOpacity(1.0);
            }
        } else {
            timerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8d949e;");
            timerLabel.setOpacity(1.0);
        }
    }

    public void setTimerVisible(boolean visible) {
        timerProgress.setVisible(visible);
        timerLabel.setVisible(visible);
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
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        
        javafx.scene.text.Text timeText = new javafx.scene.text.Text(timestamp + " ");
        timeText.getStyleClass().add("log-entry-time");
        timeText.setFill(javafx.scene.paint.Color.web("#90949c"));

        javafx.scene.text.Text msgText = new javafx.scene.text.Text(message);
        msgText.getStyleClass().add(highlight ? "log-entry-highlight" : "log-entry");
        if (highlight) {
            msgText.setFill(javafx.scene.paint.Color.web("#fa3e3e"));
        } else {
            msgText.setFill(javafx.scene.paint.Color.web("#4b4f56"));
        }

        javafx.scene.text.TextFlow entry = new javafx.scene.text.TextFlow(timeText, msgText);
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
            pulseTransition.play();
        } else {
            // Enemy's turn: highlight the target (player board)
            playerBoard.getStyleClass().add("active-board");
            pulseTransition.stop();
            turnLabel.setScaleX(1.0);
            turnLabel.setScaleY(1.0);
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
