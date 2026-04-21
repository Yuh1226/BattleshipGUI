package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameOverScreen extends VBox {
    public interface Listener {
        void onPlayAgain();
        void onBackToMenu();
        void onOpenSettings();
    }

    private Listener listener;
    private final Label titleLabel = new Label();
    private final Label resultLabel = new Label();
    
    private final Label shotsHeader = new Label();
    private final Label hitsHeader = new Label();
    private final Label accuracyHeader = new Label();
    private final Label sunkHeader = new Label();
    private final Label durationHeader = new Label();
    private final Label difficultyHeader = new Label();

    private final Label shotsLabel = new Label("0");
    private final Label hitsLabel = new Label("0");
    private final Label accuracyLabel = new Label("0%");
    private final Label sunkLabel = new Label("0");
    private final Label durationLabel = new Label("00:00");
    private final Label difficultyLabel = new Label("-");
    
    private final Button againBtn = new Button();
    private final Button menuBtn = new Button();

    public GameOverScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(25);
        setPadding(new Insets(10, 40, 40, 40));
        getStyleClass().add("screen-root");

        titleLabel.getStyleClass().add("screen-title");
        resultLabel.getStyleClass().add("status-emphasis");

        // Stats Container
        javafx.scene.layout.GridPane statsPane = new javafx.scene.layout.GridPane();
        statsPane.setAlignment(Pos.CENTER);
        statsPane.setHgap(30);
        statsPane.setVgap(15);
        statsPane.setPadding(new Insets(25));
        statsPane.getStyleClass().add("stats-container");

        // 2 columns for a cleaner grid
        addStatRow(statsPane, shotsHeader, shotsLabel, 0, 0);
        addStatRow(statsPane, hitsHeader, hitsLabel, 1, 0);
        addStatRow(statsPane, accuracyHeader, accuracyLabel, 0, 1);
        addStatRow(statsPane, sunkHeader, sunkLabel, 1, 1);
        addStatRow(statsPane, durationHeader, durationLabel, 0, 2);
        addStatRow(statsPane, difficultyHeader, difficultyLabel, 1, 2);

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        againBtn.setPrefWidth(220);
        againBtn.getStyleClass().add("action-button");
        againBtn.setOnAction(event -> {
            if (listener != null) listener.onPlayAgain();
        });

        menuBtn.setPrefWidth(220);
        menuBtn.getStyleClass().add("secondary-button");
        menuBtn.setOnAction(event -> {
            if (listener != null) listener.onBackToMenu();
        });

        buttons.getChildren().addAll(againBtn, menuBtn);

        getChildren().addAll(titleLabel, resultLabel, statsPane, buttons);
        updateLanguage();
    }

    private void addStatRow(javafx.scene.layout.GridPane pane, Label header, Label valueLabel, int col, int row) {
        VBox box = new VBox(5, header, valueLabel);
        box.setAlignment(Pos.CENTER);
        header.getStyleClass().add("stat-label");
        valueLabel.getStyleClass().add("stat-value");
        pane.add(box, col, row);
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("mission_complete"));
        shotsHeader.setText(LocalizationManager.get("total_shots"));
        hitsHeader.setText(LocalizationManager.get("hits_confirmed"));
        accuracyHeader.setText(LocalizationManager.get("accuracy"));
        sunkHeader.setText(LocalizationManager.get("ships_sunk"));
        durationHeader.setText(LocalizationManager.get("match_duration"));
        difficultyHeader.setText(LocalizationManager.get("ai_difficulty"));
        againBtn.setText(LocalizationManager.get("new_operation"));
        menuBtn.setText(LocalizationManager.get("return_hq"));
        
        // Refresh result text if it was set
        if (resultLabel.getText().contains("win") || resultLabel.getText().contains("thắng")) {
            setResultText(LocalizationManager.get("win"));
        } else if (resultLabel.getText().contains("lose") || resultLabel.getText().contains("thua")) {
            setResultText(LocalizationManager.get("lose"));
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setResultText(String text) {
        resultLabel.setText(text);
        if (text.equals(LocalizationManager.get("win"))) {
            resultLabel.setStyle("-fx-text-fill: #1877f2; -fx-font-size: 32px; -fx-font-weight: bold;");
        } else {
            resultLabel.setStyle("-fx-text-fill: #fa3e3e; -fx-font-size: 32px; -fx-font-weight: bold;");
        }
    }

    public void setStats(int shots, int hits, int sunk, String duration, String difficulty) {
        shotsLabel.setText(String.valueOf(shots));
        hitsLabel.setText(String.valueOf(hits));
        sunkLabel.setText(String.valueOf(sunk) + " / 5");
        durationLabel.setText(duration);
        difficultyLabel.setText(difficulty);
        
        double acc = (shots > 0) ? (hits * 100.0 / shots) : 0;
        accuracyLabel.setText(String.format("%.1f%%", acc));
    }
}
