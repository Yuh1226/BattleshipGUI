package battleship.fx.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class BoardGrid extends GridPane {
    public static final int SIZE = 10;

    private final Button[][] buttons = new Button[SIZE][SIZE];

    public BoardGrid() {
        setHgap(2);
        setVgap(2);
        setPadding(new Insets(6));

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Button cell = new Button();
                cell.setPrefSize(32, 32);
                cell.setMinSize(32, 32);
                cell.setMaxSize(32, 32);
                cell.setFocusTraversable(false);
                cell.getStyleClass().add("cell-button");
                applyState(cell, "state-sea");
                buttons[r][c] = cell;
                add(cell, c, r);
            }
        }
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

    public void setOnCellDoubleClicked(CellClickListener listener) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;
                // Add event filter or handler for double click
                buttons[r][c].addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2) {
                        listener.onCellClicked(row, col);
                    }
                });
            }
        }
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
                    }
                    event.consume();
                });

                cell.setOnDragEntered(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        cell.setOpacity(0.7);
                    }
                });

                cell.setOnDragExited(event -> {
                    cell.setOpacity(1.0);
                });

                cell.setOnDragDropped(event -> {
                    javafx.scene.input.Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        listener.onDragDropped(row, col, db.getString());
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
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

        // Animation for hit/miss
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(300), cell);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public void showShipCell(int row, int col) {
        Button cell = buttons[row][col];
        applyState(cell, "state-ship");
    }

    public void markButtonAsSunk(int row, int col) {
        Button cell = buttons[row][col];
        applyState(cell, "state-sunk");
    }

    public void reset() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Button cell = buttons[r][c];
                cell.setDisable(false);
                cell.setText("");
                applyState(cell, "state-sea");
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

    private void applyState(Button cell, String stateClass) {
        cell.getStyleClass().removeAll("state-sea", "state-ship", "state-hit", "state-miss", "state-sunk");
        cell.getStyleClass().add(stateClass);
    }
}
