package battleship.fx.screens;

import battleship.fx.components.BoardGrid;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final BoardGrid setupBoard = new BoardGrid();
    private final HBox shipDock = new HBox(10);
    private final Label remainingLabel = new Label();
    private final Label statusLabel = new Label();
    private final Button rotateButton = new Button("Rotate: Horizontal");
    private final Button continueButton = new Button("Continue");

    public SetupScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        Label title = new Label("FLEET DEPLOYMENT");
        title.getStyleClass().add("screen-title");

        Label hint = new Label("Position your naval assets on the grid.");
        hint.getStyleClass().add("screen-subtitle");

        HBox controlButtons = new HBox(15);
        controlButtons.setAlignment(Pos.CENTER);

        rotateButton.setPrefWidth(160);
        rotateButton.getStyleClass().add("secondary-button");
        rotateButton.setOnAction(event -> {
            for (Listener listener : listeners) {
                listener.onRotate();
            }
        });

        Button random = new Button("Auto-Deploy");
        random.setPrefWidth(160);
        random.getStyleClass().add("secondary-button");
        random.setOnAction(event -> {
            for (Listener listener : listeners) {
                listener.onRandomize();
            }
        });

        Button reset = new Button("Clear Board");
        reset.setPrefWidth(160);
        reset.getStyleClass().add("secondary-button");
        reset.setOnAction(event -> {
            for (Listener listener : listeners) {
                listener.onReset();
            }
        });

        controlButtons.getChildren().addAll(rotateButton, random, reset);

        shipDock.setAlignment(Pos.CENTER);
        shipDock.setSpacing(20);
        shipDock.setPadding(new Insets(10));
        shipDock.getStyleClass().add("ship-dock");

        VBox shipSelection = new VBox(10, new Label("AVAILABLE FLEET - DRAG OR CLICK TO SELECT"), shipDock);
        shipSelection.setAlignment(Pos.CENTER);
        ((Label)shipSelection.getChildren().get(0)).getStyleClass().add("info-label");
        shipSelection.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-radius: 15; -fx-padding: 15;");

        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER);
        actions.getStyleClass().add("setup-actions");

        Button back = new Button("Cancel");
        back.setPrefWidth(120);
        back.getStyleClass().add("secondary-button");
        back.setOnAction(event -> {
            for (Listener listener : listeners) {
                listener.onBack();
            }
        });

        continueButton.setText("START BATTLE");
        continueButton.setPrefWidth(180);
        continueButton.getStyleClass().add("action-button");
        continueButton.setOnAction(event -> {
            for (Listener listener : listeners) {
                listener.onContinue();
            }
        });

        actions.getChildren().addAll(back, continueButton);

        remainingLabel.getStyleClass().add("info-label");
        statusLabel.getStyleClass().add("status-emphasis");
        setupBoard.getStyleClass().add("board-container");
        
        HBox boardWrapper = new HBox(setupBoard);
        boardWrapper.setAlignment(Pos.CENTER);
        
        // Stack elements vertically: Title -> Board -> Selection -> Controls -> Actions
        getChildren().addAll(title, hint, statusLabel, boardWrapper, shipSelection, controlButtons, actions);
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
            HBox shipVisual = new HBox(2);
            shipVisual.getStyleClass().add("draggable-ship");
            if (length == selectedLength) {
                shipVisual.setStyle("-fx-cursor: hand; -fx-padding: 5; -fx-background-color: rgba(0, 172, 193, 0.2); -fx-background-radius: 5; -fx-border-color: #00ACC1; -fx-border-radius: 5; -fx-border-width: 1;");
            } else {
                shipVisual.setStyle("-fx-cursor: hand; -fx-padding: 5; -fx-border-color: transparent; -fx-border-width: 1;");
            }

            for(int i = 0; i < length; i++) {
                Button cell = new Button();
                cell.setPrefSize(20, 20);
                cell.setMinSize(20, 20);
                cell.setMaxSize(20, 20);
                cell.getStyleClass().addAll("cell-button", "state-ship");
                cell.setFocusTraversable(false);
                cell.setMouseTransparent(true);
                shipVisual.getChildren().add(cell);
            }
            
            // Selection by click
            shipVisual.setOnMouseClicked(event -> {
                for (Listener listener : listeners) {
                    listener.onShipSelected(length);
                }
            });

            // Selection by drag
            shipVisual.setOnDragDetected(event -> {
                javafx.scene.input.Dragboard db = shipVisual.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(String.valueOf(length));
                db.setContent(content);
                event.consume();
            });
            
            shipDock.getChildren().add(shipVisual);
        }
        remainingLabel.setText("Ships to place: " + remainingShips.size());
    }

    public void setRotationLabel(boolean vertical) {
        rotateButton.setText(vertical ? "Rotate: Vertical" : "Rotate: Horizontal");
    }

    public void setContinueEnabled(boolean enabled) {
        continueButton.setDisable(!enabled);
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }
}
