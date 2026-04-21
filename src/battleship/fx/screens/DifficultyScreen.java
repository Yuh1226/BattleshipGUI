package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        void onOpenSettings();
    }

    private Listener listener;
    private final Label titleLabel = new Label();
    private final Button easyBtn = new Button();
    private final Button normalBtn = new Button();
    private final Button hardBtn = new Button();
    private final Button backBtn = new Button();

    public DifficultyScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(20, 40, 40, 40));
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

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        easyBtn.getStyleClass().add("action-button");
        normalBtn.getStyleClass().add("action-button");
        hardBtn.getStyleClass().add("action-button");

        easyBtn.setOnAction(event -> notifyDifficulty(Difficulty.EASY));
        normalBtn.setOnAction(event -> notifyDifficulty(Difficulty.NORMAL));
        hardBtn.setOnAction(event -> notifyDifficulty(Difficulty.HARD));

        buttons.getChildren().addAll(easyBtn, normalBtn, hardBtn);

        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(event -> {
            if (listener != null) {
                listener.onBack();
            }
        });

        getChildren().addAll(topBar, titleLabel, buttons, backBtn);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("choose_difficulty"));
        easyBtn.setText(LocalizationManager.get("easy"));
        normalBtn.setText(LocalizationManager.get("normal"));
        hardBtn.setText(LocalizationManager.get("hard"));
        backBtn.setText(LocalizationManager.get("back"));
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
