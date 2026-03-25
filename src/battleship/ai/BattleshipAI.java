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

	// 1. HÀM QUYẾT ĐỊNH MỤC TIÊU BẮN
	public Node determineTarget(Board playerBoard) {
		Node[][] p1Grid = playerBoard.getGrid();
		Node targetNode = null;

		if (hitLocation.isEmpty()) {
			targetNode = getRandomNode(p1Grid);
		} else {
			if (dirShip == Ship.UNSET) {
				targetNode = guessShip(p1Grid, hitLocation.get(hitLocation.size() - 1));
				if (targetNode == null) {
					clearBotMemory();
					targetNode = getRandomNode(p1Grid);
				}
			} else {
				targetNode = fireAlongDirection(p1Grid);
			}
		}
		return targetNode;
	}

	// 2. HÀM CẬP NHẬT TRÍ NHỚ SAU KHI BẮN
	public void updateAfterFire(Board playerBoard, Node targetNode, boolean isHit) {
		if (isHit) {
			hitLocation.add(targetNode);

			if (dirShip == Ship.UNSET) {
				dirShip = guessDirShip();
			}

			// Nếu tàu chìm, xóa trí nhớ liên quan
			if (playerBoard.isSunkAt(targetNode.getX(), targetNode.getY())) {
				removeSunkShipFromMemory(playerBoard);
				if (hitLocation.isEmpty()) {
					dirShip = Ship.UNSET;
				}
			}
		}
	}


	private Node getRandomNode(Node[][] p1Grid) {
		Random rd = new Random();
		int r, c;
		Node targetNode;
		do {
			r = rd.nextInt(10);
			c = rd.nextInt(10);
			targetNode = p1Grid[r][c];
		} while (targetNode.getVal() == Node.HIT || targetNode.getVal() == Node.MISS);
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