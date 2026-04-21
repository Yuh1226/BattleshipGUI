package battleship.fx.screens;

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
    }

    private Listener listener;
    private final BoardGrid playerBoard = new BoardGrid();
    private final BoardGrid enemyBoard = new BoardGrid();
    private final Label turnLabel = new Label("Your turn");
    private final Label statusLabel = new Label("Pick a target.");

    public BattleScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        Label title = new Label("Battle");
        title.getStyleClass().add("screen-title");

        turnLabel.getStyleClass().add("status-emphasis");
        statusLabel.getStyleClass().add("info-label");

        HBox boards = new HBox(24);
        boards.setAlignment(Pos.CENTER);
        boards.getStyleClass().add("battle-boards");

        VBox playerBox = new VBox(8, new Label("Player"), playerBoard);
        playerBox.setAlignment(Pos.CENTER);
        ((Label) playerBox.getChildren().get(0)).getStyleClass().add("board-title");
        playerBoard.getStyleClass().add("board-container");

        VBox enemyBox = new VBox(8, new Label("Enemy"), enemyBoard);
        enemyBox.setAlignment(Pos.CENTER);
        ((Label) enemyBox.getChildren().get(0)).getStyleClass().add("board-title");
        enemyBoard.getStyleClass().add("board-container");

        boards.getChildren().addAll(playerBox, enemyBox);

        Button back = new Button("Back to Menu");
        back.getStyleClass().add("secondary-button");
        back.setOnAction(event -> {
            if (listener != null) {
                listener.onBackToMenu();
            }
        });

        getChildren().addAll(title, turnLabel, statusLabel, boards, back);
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
