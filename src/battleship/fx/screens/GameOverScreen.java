package battleship.fx.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameOverScreen extends VBox {
    public interface Listener {
        void onPlayAgain();
        void onBackToMenu();
    }

    private Listener listener;
    private final Label resultLabel = new Label("Game Over");

    public GameOverScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(50));
        getStyleClass().add("screen-root");

        Label title = new Label("MISSION COMPLETE");
        title.getStyleClass().add("screen-title");

        resultLabel.getStyleClass().add("status-emphasis");
        resultLabel.setStyle("-fx-font-size: 24px;");

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button again = new Button("New Operation");
        again.setPrefWidth(200);
        again.getStyleClass().add("action-button");
        again.setOnAction(event -> {
            if (listener != null) {
                listener.onPlayAgain();
            }
        });

        Button menu = new Button("Return to HQ");
        menu.setPrefWidth(200);
        menu.getStyleClass().add("secondary-button");
        menu.setOnAction(event -> {
            if (listener != null) {
                listener.onBackToMenu();
            }
        });

        buttons.getChildren().addAll(again, menu);

        getChildren().addAll(title, resultLabel, buttons);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setResultText(String text) {
        resultLabel.setText(text);
    }
}
