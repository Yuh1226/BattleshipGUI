package battleship.gdx.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import battleship.model.Board;
import battleship.model.Node;

public class BoardActor extends Actor {
	public enum CellState {
		EMPTY,
		SHIP,
		MISS,
		HIT,
		SUNK
	}

	private final int size;
	private final float cellSize;
	private final float gap;
	private final float totalSize;
	private final CellState[][] cells;
	private final ShapeRenderer renderer;
	private int highlightRow = -1;
	private int highlightCol = -1;
	private final Color highlightColor = new Color(0.15f, 0.15f, 0.15f, 1f);

	private final Color waterColor = new Color(0.12f, 0.47f, 0.78f, 1f);
	private final Color shipColor = new Color(0.35f, 0.35f, 0.35f, 1f);
	private final Color missColor = new Color(0.78f, 0.78f, 0.78f, 1f);
	private final Color hitColor = new Color(0.86f, 0.23f, 0.25f, 1f);
	private final Color sunkColor = new Color(0.10f, 0.10f, 0.10f, 1f);
	private final Color gridColor = new Color(0.05f, 0.08f, 0.14f, 1f);
	private final Color sunkMarkColor = new Color(0.90f, 0.90f, 0.90f, 1f);

	public BoardActor(int size, float cellSize, float gap) {
		this.size = size;
		this.cellSize = cellSize;
		this.gap = gap;
		this.totalSize = size * cellSize + (size - 1) * gap;
		this.cells = new CellState[size][size];
		this.renderer = new ShapeRenderer();
		clear();
		setSize(totalSize, totalSize);
	}

	public void clear() {
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				cells[r][c] = CellState.EMPTY;
			}
		}
	}

	public void setCellState(int row, int col, CellState state) {
		if (row < 0 || row >= size || col < 0 || col >= size) {
			return;
		}
		cells[row][col] = state;
	}

	public CellState getCellState(int row, int col) {
		if (row < 0 || row >= size || col < 0 || col >= size) {
			return CellState.EMPTY;
		}
		return cells[row][col];
	}

	public void toggleDemoState(int row, int col) {
		CellState current = getCellState(row, col);
		switch (current) {
			case EMPTY:
				setCellState(row, col, CellState.MISS);
				break;
			case SHIP:
				setCellState(row, col, CellState.HIT);
				break;
			case MISS:
				setCellState(row, col, CellState.HIT);
				break;
			case HIT:
				setCellState(row, col, CellState.SUNK);
				break;
			default:
				setCellState(row, col, CellState.EMPTY);
				break;
		}
	}

	public void loadFromBoard(Board board, boolean showShips) {
		Node[][] grid = board.getGrid();
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				int val = grid[r][c].getVal();
				switch (val) {
					case Node.SHIP:
						cells[r][c] = showShips ? CellState.SHIP : CellState.EMPTY;
						break;
					case Node.HIT:
						cells[r][c] = CellState.HIT;
						break;
					case Node.MISS:
						cells[r][c] = CellState.MISS;
						break;
					case Node.SUNK:
						cells[r][c] = CellState.SUNK;
						break;
					default:
						cells[r][c] = CellState.EMPTY;
						break;
				}
			}
		}
	}

	public void setHighlight(int row, int col, Color color) {
		if (row < 0 || row >= size || col < 0 || col >= size) {
			highlightRow = -1;
			highlightCol = -1;
			return;
		}
		highlightRow = row;
		highlightCol = col;
		highlightColor.set(color);
	}

	public void clearHighlight() {
		highlightRow = -1;
		highlightCol = -1;
	}

	public int[] cellAt(float localX, float localY) {
		float cellSpan = cellSize + gap;
		int col = (int) (localX / cellSpan);
		int row = (int) (localY / cellSpan);
		float offsetX = localX - col * cellSpan;
		float offsetY = localY - row * cellSpan;
		if (col < 0 || col >= size || row < 0 || row >= size) {
			return null;
		}
		if (offsetX > cellSize || offsetY > cellSize) {
			return null;
		}
		return new int[] { row, col };
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeRenderer.ShapeType.Filled);

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				Color color = waterColor;
				CellState state = cells[r][c];
				switch (state) {
					case SHIP:
						color = shipColor;
						break;
					case MISS:
						color = missColor;
						break;
					case HIT:
						color = hitColor;
						break;
					case SUNK:
						color = sunkColor;
						break;
					default:
						color = waterColor;
						break;
				}
				renderer.setColor(color);
				float x = getX() + c * (cellSize + gap);
				float y = getY() + r * (cellSize + gap);
				renderer.rect(x, y, cellSize, cellSize);
			}
		}
		renderer.end();

		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.setColor(gridColor);
		for (int r = 0; r <= size; r++) {
			float y = getY() + r * (cellSize + gap) - gap;
			if (r == 0) {
				y = getY();
			}
			if (r == size) {
				y = getY() + totalSize;
			}
			renderer.line(getX(), y, getX() + totalSize, y);
		}
		for (int c = 0; c <= size; c++) {
			float x = getX() + c * (cellSize + gap) - gap;
			if (c == 0) {
				x = getX();
			}
			if (c == size) {
				x = getX() + totalSize;
			}
			renderer.line(x, getY(), x, getY() + totalSize);
		}

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (cells[r][c] == CellState.SUNK) {
					float x = getX() + c * (cellSize + gap);
					float y = getY() + r * (cellSize + gap);
					float inset = cellSize * 0.2f;
					renderer.setColor(sunkMarkColor);
					renderer.line(x + inset, y + inset, x + cellSize - inset, y + cellSize - inset);
					renderer.line(x + inset, y + cellSize - inset, x + cellSize - inset, y + inset);
				}
			}
		}

		if (highlightRow >= 0 && highlightCol >= 0) {
			float x = getX() + highlightCol * (cellSize + gap);
			float y = getY() + highlightRow * (cellSize + gap);
			renderer.setColor(highlightColor);
			renderer.rect(x + 1, y + 1, cellSize - 2, cellSize - 2);
		}
		renderer.end();
		batch.begin();
	}

	public void dispose() {
		renderer.dispose();
	}
}
