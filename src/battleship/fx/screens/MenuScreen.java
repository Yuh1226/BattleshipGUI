package battleship.fx.screens;

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
        void onHelp();
    }

    private Listener listener;
    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final Button startButton = new Button();
    private final Button helpButton = new Button();

    public MenuScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        title.getStyleClass().add("screen-title");
        subtitle.getStyleClass().add("screen-subtitle");

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        startButton.setPrefWidth(280);
        startButton.getStyleClass().add("action-button");
        startButton.setOnAction(event -> {
            if (listener != null) {
                listener.onStart();
            }
        });

        helpButton.setPrefWidth(280);
        helpButton.getStyleClass().add("secondary-button");
        helpButton.setOnAction(event -> {
            if (listener != null) {
                listener.onHelp();
            }
        });

        buttons.getChildren().addAll(startButton, helpButton);

        VBox content = new VBox(10, title, subtitle);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(content, buttons);
        updateLanguage();
    }

    public void updateLanguage() {
        title.setText("BATTLESHIP");
        subtitle.setText(LocalizationManager.get("tactical_warfare"));
        startButton.setText(LocalizationManager.get("start_mission"));
        helpButton.setText(LocalizationManager.get("help"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
