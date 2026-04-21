package battleship.fx.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DifficultyScreen extends VBox {
    public enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    public interface Listener {
        void onDifficultySelected(Difficulty difficulty);
        void onBack();
    }

    private Listener listener;

    public DifficultyScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        Label title = new Label("Choose Difficulty");
        title.getStyleClass().add("screen-title");

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        Button easy = new Button("Easy");
        Button normal = new Button("Normal");
        Button hard = new Button("Hard");
        easy.getStyleClass().add("action-button");
        normal.getStyleClass().add("action-button");
        hard.getStyleClass().add("action-button");

        easy.setOnAction(event -> notifyDifficulty(Difficulty.EASY));
        normal.setOnAction(event -> notifyDifficulty(Difficulty.NORMAL));
        hard.setOnAction(event -> notifyDifficulty(Difficulty.HARD));

        buttons.getChildren().addAll(easy, normal, hard);

        Button back = new Button("Back");
        back.getStyleClass().add("secondary-button");
        back.setOnAction(event -> {
            if (listener != null) {
                listener.onBack();
            }
        });

        getChildren().addAll(title, buttons, back);
    }

    private void notifyDifficulty(Difficulty difficulty) {
        if (listener != null) {
            listener.onDifficultySelected(difficulty);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
