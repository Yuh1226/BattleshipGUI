package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class HelpScreen extends VBox {
    public interface Listener {
        void onBack();
    }

    private Listener listener;
    private final Label titleLabel = new Label();
    private final Label rulesTitle = new Label();
    private final Label rulesContent = new Label();
    private final Label placementTitle = new Label();
    private final Label placementContent = new Label();
    private final Button backButton = new Button();

    public HelpScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(30, 50, 40, 50));
        getStyleClass().add("screen-root");

        titleLabel.getStyleClass().add("screen-title");

        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("settings-container"); // Reuse styling

        rulesTitle.getStyleClass().add("stat-label");
        rulesTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        rulesContent.getStyleClass().add("info-label");
        rulesContent.setWrapText(true);

        placementTitle.getStyleClass().add("stat-label");
        placementTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        placementContent.getStyleClass().add("info-label");
        placementContent.setWrapText(true);

        container.getChildren().addAll(rulesTitle, rulesContent, placementTitle, placementContent);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.getStyleClass().add("log-container"); // Reuse styling

        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(e -> {
            if (listener != null) listener.onBack();
        });

        getChildren().addAll(titleLabel, scrollPane, backButton);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("help"));
        rulesTitle.setText(LocalizationManager.get("rules_title"));
        rulesContent.setText(LocalizationManager.get("rules_content"));
        placementTitle.setText(LocalizationManager.get("placement_title"));
        placementContent.setText(LocalizationManager.get("placement_content"));
        backButton.setText(LocalizationManager.get("back"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
