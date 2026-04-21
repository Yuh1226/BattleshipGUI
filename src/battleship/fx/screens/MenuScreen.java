package battleship.fx.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import battleship.fx.LocalizationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {
    public interface Listener {
        void onStart();
        void onSettings();
    }

    private Listener listener;
    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final Button startButton = new Button();
    private final Button settingsButton = new Button();

    public MenuScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        title.getStyleClass().add("screen-title");
        subtitle.getStyleClass().add("screen-subtitle");

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        startButton.setPrefWidth(220);
        startButton.getStyleClass().add("action-button");
        startButton.setOnAction(event -> {
            if (listener != null) {
                listener.onStart();
            }
        });

        settingsButton.setPrefWidth(220);
        settingsButton.getStyleClass().add("secondary-button");
        settingsButton.setOnAction(event -> {
            if (listener != null) {
                listener.onSettings();
            }
        });

        buttons.getChildren().addAll(startButton, settingsButton);

        VBox content = new VBox(10, title, subtitle);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(content, buttons);
        updateLanguage();
    }

    public void updateLanguage() {
        title.setText("BATTLESHIP"); // Keep original or translate
        subtitle.setText(LocalizationManager.get("tactical_warfare"));
        startButton.setText(LocalizationManager.get("start_mission"));
        settingsButton.setText(LocalizationManager.get("settings"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
