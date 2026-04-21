package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import battleship.fx.components.BoardGrid;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SetupScreen extends VBox {
    public interface Listener {
        void onBack();
        void onContinue();
        void onRotate();
        void onRandomize();
        void onReset();
        void onShipSelected(int length);
        void onOpenSettings();
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final BoardGrid setupBoard = new BoardGrid();
    private final HBox shipDock = new HBox(10);
    private final Label titleLabel = new Label();
    private final Label hintLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label fleetLabel = new Label();
    private final Button rotateButton = new Button();
    private final Button randomButton = new Button();
    private final Button resetButton = new Button();
    private final Button continueButton = new Button();
    private final Button backButton = new Button();

    private boolean isVertical = false;

    public SetupScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(5);
        setPadding(new Insets(5, 20, 10, 20));
        getStyleClass().add("screen-root");

        // Top bar for settings
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        Button settingsBtn = new Button("⚙");
        settingsBtn.getStyleClass().add("secondary-button");
        settingsBtn.setStyle("-fx-font-size: 14px; -fx-padding: 2 6;");
        settingsBtn.setOnAction(e -> { for(Listener l : listeners) l.onOpenSettings(); });
        topBar.getChildren().add(settingsBtn);

        titleLabel.getStyleClass().add("screen-title");
        hintLabel.getStyleClass().add("screen-subtitle");

        HBox controlButtons = new HBox(8);
        controlButtons.setAlignment(Pos.CENTER);

        rotateButton.setPrefWidth(130);
        rotateButton.getStyleClass().add("secondary-button");
        rotateButton.setOnAction(event -> {
            for (Listener listener : listeners) listener.onRotate();
        });

        randomButton.setPrefWidth(130);
        randomButton.getStyleClass().add("secondary-button");
        randomButton.setOnAction(event -> {
            for (Listener listener : listeners) listener.onRandomize();
        });

        resetButton.setPrefWidth(130);
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setOnAction(event -> {
            for (Listener listener : listeners) listener.onReset();
        });

        controlButtons.getChildren().addAll(rotateButton, randomButton, resetButton);

        shipDock.setAlignment(Pos.CENTER);
        shipDock.setSpacing(10);
        shipDock.setPadding(new Insets(2));
        shipDock.getStyleClass().add("ship-dock");

        VBox shipSelection = new VBox(2, fleetLabel, shipDock);
        shipSelection.setAlignment(Pos.CENTER);
        fleetLabel.getStyleClass().add("info-label");
        shipSelection.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 8; -fx-padding: 5;");

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        actions.getStyleClass().add("setup-actions");

        backButton.setPrefWidth(90);
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            for (Listener listener : listeners) listener.onBack();
        });

        continueButton.setPrefWidth(150);
        continueButton.getStyleClass().add("action-button");
        continueButton.setOnAction(event -> {
            for (Listener listener : listeners) listener.onContinue();
        });

        actions.getChildren().addAll(backButton, continueButton);

        statusLabel.getStyleClass().add("status-emphasis");
        setupBoard.getStyleClass().addAll("board-container", "player-board", "active-board");
        
        HBox boardWrapper = new HBox(setupBoard);
        boardWrapper.setAlignment(Pos.CENTER);
        
        getChildren().addAll(topBar, titleLabel, hintLabel, statusLabel, boardWrapper, shipSelection, controlButtons, actions);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("fleet_deployment"));
        hintLabel.setText(LocalizationManager.get("position_assets"));
        fleetLabel.setText(LocalizationManager.get("available_fleet"));
        randomButton.setText(LocalizationManager.get("auto_deploy"));
        resetButton.setText(LocalizationManager.get("clear_board"));
        backButton.setText(LocalizationManager.get("cancel"));
        continueButton.setText(LocalizationManager.get("start_battle"));
        setRotationLabel(isVertical);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public BoardGrid getBoard() {
        return setupBoard;
    }

    public void setShipsRemaining(List<Integer> remainingShips, int selectedLength) {
        shipDock.getChildren().clear();
        for (Integer length : remainingShips) {
            HBox shipVisual = new HBox(1);
            shipVisual.getStyleClass().add("draggable-ship");
            if (length == selectedLength) {
                shipVisual.setStyle("-fx-cursor: hand; -fx-padding: 3; -fx-background-color: rgba(0, 172, 193, 0.2); -fx-background-radius: 5; -fx-border-color: #00ACC1; -fx-border-radius: 5; -fx-border-width: 1;");
            } else {
                shipVisual.setStyle("-fx-cursor: hand; -fx-padding: 3; -fx-border-color: transparent; -fx-border-width: 1;");
            }

            for(int i = 0; i < length; i++) {
                Button cell = new Button();
                cell.setPrefSize(16, 16);
                cell.setMinSize(16, 16);
                cell.setMaxSize(16, 16);
                cell.getStyleClass().addAll("cell-button", "state-ship");
                cell.setFocusTraversable(false);
                cell.setMouseTransparent(true);
                shipVisual.getChildren().add(cell);
            }
            
            shipVisual.setOnMouseClicked(event -> {
                for (Listener listener : listeners) listener.onShipSelected(length);
            });

            shipVisual.setOnDragDetected(event -> {
                javafx.scene.input.Dragboard db = shipVisual.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(String.valueOf(length));
                db.setContent(content);
                event.consume();
            });
            
            shipDock.getChildren().add(shipVisual);
        }
    }

    public void setRotationLabel(boolean vertical) {
        this.isVertical = vertical;
        rotateButton.setText(vertical ? LocalizationManager.get("rotate_v") : LocalizationManager.get("rotate_h"));
    }

    public void setContinueEnabled(boolean enabled) {
        continueButton.setDisable(!enabled);
    }

    public void setStatusText(String text) {
        // This might need translation too, but it often comes from logic
        statusLabel.setText(text);
    }
}
