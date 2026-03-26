package battleship.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import battleship.model.Board;
import battleship.model.Node;
import battleship.model.Ship;

public class BattleshipAI {
	private List<Node> hitLocation = new ArrayList<>();
	private int dirShip = Ship.UNSET;

	public static final int EASY = 1;
	public static final int MEDIUM = 2;
	public static final int HARD = 3;

	// HÀM QUYẾT ĐỊNH MỤC TIÊU BẮN
	public Node determineTarget(Board p1board, Integer level, List<Integer> aliveShips) {
		Node[][] p1Grid = p1board.getGrid();
		Node targetNode = null;

		if (hitLocation.isEmpty()) {
			if (level != HARD) {
				targetNode = getRandomNode(p1Grid);
			} else {
				targetNode = targetHeatMap(p1Grid, aliveShips);
			}
		} else {
			if (dirShip == Ship.UNSET) {
				if (level == HARD) {
					targetNode = targetHeatMap(p1Grid, aliveShips); // Hard: Tính xác suất ô kế tiếp
				} else {
					targetNode = guessShip(p1Grid, hitLocation.get(hitLocation.size() - 1)); // Medium: Random 4 hướng
				}
				if (targetNode == null) {
					clearBotMemory();
					if (level == HARD) {
						targetNode = targetHeatMap(p1Grid, aliveShips);
					} else {
						targetNode = getRandomNode(p1Grid);
					}
				}
			} else {
				targetNode = fireAlongDirection(p1Grid);
			}
		}
		return targetNode;
	}
	
	// HÀM CẬP NHẬT TRÍ NHỚ SAU KHI BẮN
	public void updateAfterFire(Board p1board, Node targetNode, boolean isHit) {
		if (isHit) {
			hitLocation.add(targetNode);

			if (dirShip == Ship.UNSET) {
				dirShip = guessDirShip();
			}

			// Nếu tàu chìm, xóa trí nhớ liên quan
			if (p1board.isSunkAt(targetNode.getX(), targetNode.getY())) {
				removeSunkShipFromMemory(p1board);
				if (hitLocation.isEmpty()) {
					dirShip = Ship.UNSET;
				}
			}
		}
	}

	// họn mục tiêu cho bản đồ nhiệt
	public Node targetHeatMap(Node[][] p1Grid, List<Integer> aliveShips) {
		int[][] heatmap = new int[10][10];
		Node targetNode = null;

		for (int shipLen : aliveShips) {
			for (int r = 0; r < 10; r++) {
				for (int c = 0; c < 10; c++) {

					// Thử đặt Ngang
					if (canPlaceHypothetical(p1Grid, shipLen, r, c, Ship.HORIZONTAL)) {
						addWeightToHeatmap(p1Grid, shipLen, r, c, heatmap, Ship.HORIZONTAL);
					}

					// Thử đặt Dọc
					if (canPlaceHypothetical(p1Grid, shipLen, r, c, Ship.VERTICAl)) {
						addWeightToHeatmap(p1Grid, shipLen, r, c, heatmap, Ship.VERTICAl);
					}
				}
			}
		}

		return getHighestScoreNode(p1Grid, heatmap);
	}

	public boolean canPlaceHypothetical(Node[][] p1Grid, int length, int row, int col, int direction) {
		for (int i = 0; i < length; i++) {
			int r = row + (direction == Ship.VERTICAl ? i : 0);
			int c = col + (direction == Ship.HORIZONTAL ? i : 0);

			if (r < 0 || r >= 10 || c < 0 || c >= 10)
				return false;

			if (p1Grid[r][c].getVal() == Node.MISS || p1Grid[r][c].getVal() == Node.SUNK)
				return false;
		}
		return true;
	}

	// Thêm trọng số cho bản đồ nhiệt
	public void addWeightToHeatmap(Node[][] p1Grid, int shipLen, int r, int c, int[][] heatmap, int direction) {
		int weight = 1; // Điểm cơ bản

		for (int i = 0; i < shipLen; i++) {
			int checkR = r + (direction == Ship.VERTICAl ? i : 0);
			int checkC = c + (direction == Ship.HORIZONTAL ? i : 0);
			if (p1Grid[checkR][checkC].getVal() == Node.HIT) {
				weight += 50; // Cộng siêu trọng số
			}
		}

		for (int i = 0; i < shipLen; i++) {
			if (direction == Ship.HORIZONTAL) {
				heatmap[r][c + i] += weight;
			} else {
				heatmap[r + i][c] += weight;
			}
		}
	}

	// Lấy trọng số cao nhất
	public Node getHighestScoreNode(Node[][] p1Grid, int[][] heatmap) {
		int maxHeat = -1;
		int row = 0;
		int col = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				int val = p1Grid[i][j].getVal();

				if (val != Node.HIT && val != Node.MISS && val != Node.SUNK) {
					if (heatmap[i][j] > maxHeat) {
						maxHeat = heatmap[i][j];
						row = i;
						col = j;
					}
				}
			}
		}
		return p1Grid[row][col];
	}

	private Node getRandomNode(Node[][] p1Grid) {
		Random rd = new Random();
		int r, c;
		Node targetNode;
		int attempts = 0;
		do {
			r = rd.nextInt(10);
			c = rd.nextInt(10);
			targetNode = p1Grid[r][c];
			targetNode = p1Grid[r][c];
			attempts++;
		} while (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS
				|| targetNode.getVal() == Node.SUNK);
		return targetNode;
	}

	private int guessDirShip() {
		if (hitLocation.size() < 2) {
			return Ship.UNSET;
		}
		Node lastHit = hitLocation.get(hitLocation.size() - 1);
		Node firstHit = hitLocation.get(0);

		if (lastHit.getX() == firstHit.getX()) {
			return Ship.HORIZONTAL;
		} else if (lastHit.getY() == firstHit.getY()) {
			return Ship.VERTICAl;
		}
		return Ship.UNSET;
	}

	private Node guessShip(Node[][] p1Grid, Node targetNode) {
		List<Node> validTargets = new ArrayList<>();
		int r = targetNode.getX();
		int c = targetNode.getY();

		if (r - 1 >= 0 && p1Grid[r - 1][c].getVal() != Node.HIT && p1Grid[r - 1][c].getVal() != Node.MISS)
			validTargets.add(p1Grid[r - 1][c]);
		if (r + 1 < 10 && p1Grid[r + 1][c].getVal() != Node.HIT && p1Grid[r + 1][c].getVal() != Node.MISS)
			validTargets.add(p1Grid[r + 1][c]);
		if (c - 1 >= 0 && p1Grid[r][c - 1].getVal() != Node.HIT && p1Grid[r][c - 1].getVal() != Node.MISS)
			validTargets.add(p1Grid[r][c - 1]);
		if (c + 1 < 10 && p1Grid[r][c + 1].getVal() != Node.HIT && p1Grid[r][c + 1].getVal() != Node.MISS)
			validTargets.add(p1Grid[r][c + 1]);

		if (!validTargets.isEmpty()) {
			Random rd = new Random();
			return validTargets.get(rd.nextInt(validTargets.size()));
		}
		return null;
	}

	private Node fireAlongDirection(Node[][] p1Grid) {
		Node lastHit = hitLocation.get(hitLocation.size() - 1);
		int r = lastHit.getX();
		int c = lastHit.getY();
		List<Node> validTargets = new ArrayList<>();

		if (dirShip == Ship.HORIZONTAL) {
			if (c - 1 >= 0 && p1Grid[r][c - 1].getVal() != Node.HIT && p1Grid[r][c - 1].getVal() != Node.MISS)
				validTargets.add(p1Grid[r][c - 1]);
			if (c + 1 < 10 && p1Grid[r][c + 1].getVal() != Node.HIT && p1Grid[r][c + 1].getVal() != Node.MISS)
				validTargets.add(p1Grid[r][c + 1]);
		} else if (dirShip == Ship.VERTICAl) {
			if (r - 1 >= 0 && p1Grid[r - 1][c].getVal() != Node.HIT && p1Grid[r - 1][c].getVal() != Node.MISS)
				validTargets.add(p1Grid[r - 1][c]);
			if (r + 1 < 10 && p1Grid[r + 1][c].getVal() != Node.HIT && p1Grid[r + 1][c].getVal() != Node.MISS)
				validTargets.add(p1Grid[r + 1][c]);
		}

		if (!validTargets.isEmpty()) {
			return validTargets.get(0);
		} else {
			Node firstHit = hitLocation.get(0);
			int rFirst = firstHit.getX();
			int cFirst = firstHit.getY();
			List<Node> fallbackTargets = new ArrayList<>();

			if (dirShip == Ship.HORIZONTAL) {
				if (cFirst - 1 >= 0 && p1Grid[rFirst][cFirst - 1].getVal() != Node.HIT
						&& p1Grid[rFirst][cFirst - 1].getVal() != Node.MISS)
					fallbackTargets.add(p1Grid[rFirst][cFirst - 1]);
				if (cFirst + 1 < 10 && p1Grid[rFirst][cFirst + 1].getVal() != Node.HIT
						&& p1Grid[rFirst][cFirst + 1].getVal() != Node.MISS)
					fallbackTargets.add(p1Grid[rFirst][cFirst + 1]);
			} else if (dirShip == Ship.VERTICAl) {
				if (rFirst - 1 >= 0 && p1Grid[rFirst - 1][cFirst].getVal() != Node.HIT
						&& p1Grid[rFirst - 1][cFirst].getVal() != Node.MISS)
					fallbackTargets.add(p1Grid[rFirst - 1][cFirst]);
				if (rFirst + 1 < 10 && p1Grid[rFirst + 1][cFirst].getVal() != Node.HIT
						&& p1Grid[rFirst + 1][cFirst].getVal() != Node.MISS)
					fallbackTargets.add(p1Grid[rFirst + 1][cFirst]);
			}

			if (!fallbackTargets.isEmpty()) {
				hitLocation.add(firstHit);
				return fallbackTargets.get(0);
			} else {
				dirShip = (dirShip == Ship.HORIZONTAL) ? Ship.VERTICAl : Ship.HORIZONTAL;
				return fireAlongDirection(p1Grid);
			}
		}
	}

	private void clearBotMemory() {
		hitLocation.clear();
		dirShip = Ship.UNSET;
	}

	private void removeSunkShipFromMemory(Board p1board) {
		Iterator<Node> iterator = hitLocation.iterator();
		while (iterator.hasNext()) {
			Node n = iterator.next();
			if (p1board.isSunkAt(n.getX(), n.getY())) {
				iterator.remove();
			}
		}
	}
}