package battleship.fx.components;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BoardGrid extends GridPane {
    public static final int SIZE = 10;

    private final Button[][] buttons = new Button[SIZE][SIZE];
    
    // For enhanced Drag & Drop
    private Supplier<Integer> directionProvider; // 0 for H, 1 for V
    private PlacementValidator placementValidator;

    public BoardGrid() {
        setHgap(2);
        setVgap(2);
        setPadding(new Insets(5));

        // Add column labels (A-J) at row 0, starting from column 1
        for (int c = 0; c < SIZE; c++) {
            javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf((char)('A' + c)));
            label.getStyleClass().add("coord-label");
            label.setPrefSize(26, 26);
            label.setMinSize(26, 26);
            label.setMaxSize(26, 26);
            label.setAlignment(javafx.geometry.Pos.CENTER);
            add(label, c + 1, 0);
        }

        // Add row labels (1-10) at column 0, starting from row 1
        for (int r = 0; r < SIZE; r++) {
            javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf(r + 1));
            label.getStyleClass().add("coord-label");
            label.setPrefSize(26, 26);
            label.setMinSize(26, 26);
            label.setMaxSize(26, 26);
            label.setAlignment(javafx.geometry.Pos.CENTER);
            add(label, 0, r + 1);
        }

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Button cell = new Button();
                cell.setPrefSize(26, 26);
                cell.setMinSize(26, 26);
                cell.setMaxSize(26, 26);
                cell.setFocusTraversable(false);
                cell.getStyleClass().add("cell-button");
                applyState(cell, "state-sea");
                buttons[r][c] = cell;
                // Buttons are offset by 1 because of labels
                add(cell, c + 1, r + 1);
            }
        }
    }

    public void setDirectionProvider(Supplier<Integer> provider) {
        this.directionProvider = provider;
    }

    public void setPlacementValidator(PlacementValidator validator) {
        this.placementValidator = validator;
    }

    public void setOnCellClicked(CellClickListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                buttons[r][c].setOnMouseClicked(event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 1) {
                        listener.onCellClicked(row, col);
                    }
                });
            }
        }
    }

    public void setOnCellRightClicked(CellClickListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                buttons[r][c].addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        listener.onCellClicked(row, col);
                    }
                });
            }
        }
    }

    public void setOnCellDragDetected(CellClickListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                buttons[r][c].setOnDragDetected(event -> {
                    if (buttons[row][col].getStyleClass().contains("state-ship")) {
                        listener.onCellClicked(row, col);
                        event.consume();
                    }
                });
            }
        }
    }

    public void setOnCellDragDone(CellDragDoneListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                buttons[r][c].setOnDragDone(event -> {
                    listener.onDragDone(row, col, event.getTransferMode() != null);
                });
            }
        }
    }

    public interface CellDragDoneListener {
        void onDragDone(int row, int col, boolean success);
    }

    public void setOnCellDragDropped(CellDragDroppedListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                Button cell = buttons[r][c];

                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
                        highlightPlacement(row, col, event.getDragboard().getString());
                    }
                    event.consume();
                });

                cell.setOnDragEntered(event -> {
                    // Highlight logic handled in DragOver for continuous updates
                    event.consume();
                });

                cell.setOnDragExited(event -> {
                    clearHighlights();
                    event.consume();
                });

                cell.setOnDragDropped(event -> {
                    javafx.scene.input.Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        listener.onDragDropped(row, col, db.getString());
                        success = true;
                    }
                    clearHighlights();
                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
    }

    private void highlightPlacement(int row, int col, String data) {
        clearHighlights();
        try {
            int length = Integer.parseInt(data);
            int direction = (directionProvider != null) ? directionProvider.get() : 0; // 0: H, 1: V
            
            boolean isValid = (placementValidator != null) && placementValidator.isValid(length, row, col, direction);
            String style = isValid ? "drag-valid" : "drag-invalid";

            for (int i = 0; i < length; i++) {
                int r = row + (direction == 1 ? i : 0);
                int c = col + (direction == 0 ? i : 0);
                if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                    buttons[r][c].getStyleClass().add(style);
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void clearHighlights() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                buttons[r][c].getStyleClass().removeAll("drag-valid", "drag-invalid");
            }
        }
    }

    public Button getButton(int row, int col) {
        return buttons[row][col];
    }

    public void updateButtonState(int row, int col, boolean isHit) {
        Button cell = buttons[row][col];
        if (isHit) {
            applyState(cell, "state-hit");
        } else {
            applyState(cell, "state-miss");
        }
        cell.setDisable(true);

        // Enhanced animation
        if (isHit) {
            // Explosion effect: Scale up large then settle
            ScaleTransition st = new ScaleTransition(Duration.millis(400), cell);
            st.setFromX(2.0); st.setFromY(2.0);
            st.setToX(1.0); st.setToY(1.0);
            
            FadeTransition ft = new FadeTransition(Duration.millis(200), cell);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            
            ParallelTransition pt = new ParallelTransition(st, ft);
            pt.play();
            
            shakeCell(cell, 4, 6); // Harder shake for hit
        } else {
            // Splash effect: Scale from center
            ScaleTransition st = new ScaleTransition(Duration.millis(300), cell);
            st.setFromX(0.2); st.setFromY(0.2);
            st.setToX(1.0); st.setToY(1.0);

            FadeTransition ft = new FadeTransition(Duration.millis(300), cell);
            ft.setFromValue(0.3);
            ft.setToValue(1.0);

            ParallelTransition pt = new ParallelTransition(st, ft);
            pt.play();
        }
    }

    public void shake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(40), this);
        tt.setFromX(0);
        tt.setToX(8);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> setTranslateX(0));
        tt.play();
    }

    private void shakeCell(Button cell, int displacement, int cycles) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(40), cell);
        tt.setFromX(0);
        tt.setToX(displacement);
        tt.setCycleCount(cycles);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> cell.setTranslateX(0));
        tt.play();
    }

    private void shakeCell(Button cell) {
        shakeCell(cell, 2, 4);
    }

    public void showShipCell(int row, int col) {
        showShipCell(row, col, false);
    }

    public void showShipCell(int row, int col, boolean animate) {
        Button cell = buttons[row][col];
        applyState(cell, "state-ship");
        
        if (animate) {
            // Pop-in effect only if requested
            ScaleTransition st = new ScaleTransition(Duration.millis(250), cell);
            st.setFromX(0.4); st.setFromY(0.4);
            st.setToX(1.0); st.setToY(1.0);
            st.play();
        }
    }

    public void markButtonAsSunk(int row, int col) {
        Button cell = buttons[row][col];
        applyState(cell, "state-sunk");
        
        // Sinking tilt animation
        javafx.animation.RotateTransition rt = new javafx.animation.RotateTransition(Duration.millis(600), cell);
        rt.setByAngle(15);
        rt.setCycleCount(2);
        rt.setAutoReverse(true);
        
        FadeTransition ft = new FadeTransition(Duration.millis(600), cell);
        ft.setToValue(0.7); // Fade slightly
        
        ParallelTransition pt = new ParallelTransition(rt, ft);
        pt.play();
        
        shakeCell(cell, 3, 6);
    }

    public void reset() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Button cell = buttons[r][c];
                cell.setDisable(false);
                cell.setText("");
                applyState(cell, "state-sea");
                cell.getStyleClass().removeAll("drag-valid", "drag-invalid");
            }
        }
    }

    public void setCellEnabled(int row, int col, boolean enabled) {
        buttons[row][col].setDisable(!enabled);
    }

    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }

    public interface CellDragDroppedListener {
        void onDragDropped(int row, int col, String data);
    }

    public interface PlacementValidator {
        boolean isValid(int length, int row, int col, int direction);
    }

    private void applyState(Button cell, String stateClass) {
        cell.getStyleClass().removeAll("state-sea", "state-ship", "state-hit", "state-miss", "state-sunk");
        cell.getStyleClass().add(stateClass);
    }
}
