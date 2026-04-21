package battleship.fx.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {
    public interface Listener {
        void onStart();
    }

    private Listener listener;

    public MenuScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        Label title = new Label("BATTLESHIP");
        title.getStyleClass().add("screen-title");

        Label subtitle = new Label("Tactical Naval Warfare");
        subtitle.getStyleClass().add("screen-subtitle");

        Button startButton = new Button("START MISSION");
        startButton.setPrefWidth(220);
        startButton.getStyleClass().add("action-button");
        startButton.setOnAction(event -> {
            if (listener != null) {
                listener.onStart();
            }
        });

        VBox content = new VBox(10, title, subtitle);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(content, startButton);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
