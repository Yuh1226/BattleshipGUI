package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import battleship.fx.components.BoardGrid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BattleScreen extends VBox {
    public interface Listener {
        void onBackToMenu();
        void onOpenSettings();
    }

    private Listener listener;
    private final BoardGrid playerBoard = new BoardGrid();
    private final BoardGrid enemyBoard = new BoardGrid();
    private final Label titleLabel = new Label();
    private final Label turnLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label playerLabel = new Label("Player");
    private final Label enemyLabel = new Label("Enemy");
    private final Button backBtn = new Button();

    public BattleScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(10, 40, 40, 40));
        getStyleClass().add("screen-root");

        // Top bar for settings
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        Button settingsBtn = new Button("⚙");
        settingsBtn.getStyleClass().add("secondary-button");
        settingsBtn.setStyle("-fx-font-size: 18px; -fx-padding: 5 10;");
        settingsBtn.setOnAction(e -> { if(listener != null) listener.onOpenSettings(); });
        topBar.getChildren().add(settingsBtn);

        titleLabel.getStyleClass().add("screen-title");

        turnLabel.getStyleClass().add("status-emphasis");
        statusLabel.getStyleClass().add("info-label");

        HBox boards = new HBox(24);
        boards.setAlignment(Pos.CENTER);
        boards.getStyleClass().add("battle-boards");

        VBox playerBox = new VBox(8, playerLabel, playerBoard);
        playerBox.setAlignment(Pos.CENTER);
        playerLabel.getStyleClass().add("board-title");
        playerBoard.getStyleClass().add("board-container");

        VBox enemyBox = new VBox(8, enemyLabel, enemyBoard);
        enemyBox.setAlignment(Pos.CENTER);
        enemyLabel.getStyleClass().add("board-title");
        enemyBoard.getStyleClass().add("board-container");

        boards.getChildren().addAll(playerBox, enemyBox);

        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(event -> {
            if (listener != null) listener.onBackToMenu();
        });

        getChildren().addAll(topBar, titleLabel, turnLabel, statusLabel, boards, backBtn);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText("BATTLE"); // Or LocalizationManager.get("battle")
        backBtn.setText(LocalizationManager.get("return_hq"));
        // labels for player/enemy might need translation if you add to Manager
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

    public void setTurnText(String text) {
        turnLabel.setText(text);
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public void setEnemyEnabled(boolean enabled) {
        for (int r = 0; r < BoardGrid.SIZE; r++) {
            for (int c = 0; c < BoardGrid.SIZE; c++) {
                enemyBoard.setCellEnabled(r, c, enabled);
            }
        }
    }
}
