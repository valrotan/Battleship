package battleships;

import java.awt.Point;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Board {
	private int[][] board;
	private static final int EMPTY = 0, MISSED = 1, SHIP = 2, HIT = 3;
	private static final int ONES = 4, TWOS = 3, THREES = 2, FOURS = 1;

	public Board() {
		board = new int[10][10];
	}

	public String place(int x, int y)// changes pos on board to either 0 or 2
	{
		if (board[x][y] == 0) {
			board[x][y] = 2;
			return "shipPlaced";
		} else if (board[x][y] == 2) {
			board[x][y] = 0;
			return "shipRemoved";
		}
		return "couldNotPlaceShip";
	}

	public String attack(int x, int y) {
		
		if (board[x][y] % 2 == 0)
			board[x][y]++;
		else
			return "couldNotAttack";
		
		if (board[x][y] == 3) {
			if (!isKilled(x, y)) {
				return "hit";
			} else if (isKilled(x, y)) {
				return "killed";
			}
		} else if (board[x][y] == 1) {
			return "missed";
		}
		return "couldNotAttack";
	}
	
	// checks if the ship at a given coordinate is killed
	public boolean isKilled(int x, int y) {

		for (int i = 0; i < 4; i++)
			if (!isKilled(x, y, i))
				return false;
		markKilled(x, y, x, y);
		return true;
	}

	private boolean isKilled(int x, int y, int dir) {

		if (dir == 0) {

			if (!inBounds(x, y - 1))
				return true;

			int pos = board[x][y - 1];
			if (pos == 0 || pos == 1) {
				return true;
			} else if (pos == 2) {
				return false;
			} else {
				return isKilled(x, y - 1, dir);
			}
		} else if (dir == 1) {

			if (!inBounds(x - 1, y))
				return true;

			int pos = board[x - 1][y];
			if (pos == 0 || pos == 1) {
				return true;
			} else if (pos == 2) {
				return false;
			} else {
				return isKilled(x - 1, y, dir);
			}
		} else if (dir == 2) {

			if (!inBounds(x, y + 1))
				return true;

			int pos = board[x][y + 1];
			if (pos == 0 || pos == 1) {
				return true;
			} else if (pos == 2) {
				return false;
			} else {
				return isKilled(x, y + 1, dir);
			}
		} else if (dir == 3) {

			if (!inBounds(x + 1, y))
				return true;

			int pos = board[x + 1][y];
			if (pos == 0 || pos == 1) {
				return true;
			} else if (pos == 2) {
				return false;
			} else {
				return isKilled(x + 1, y, dir);
			}
		}
		return false;
	}
	
	// marks all places around the ship at given coordinates as shot
	public void markKilled(int lastx, int lasty, int x, int y) {
		if (!inBounds(x, y)) {
			return;
		}
		if (board[x][y] == 1) {
			return;
		}		
		if (board[x][y] == 0) {
			mark(x, y, 1);
			return;
		}
		if (!(lastx == x && lasty == y + 1)) {
			markKilled(x, y, x, y + 1);
		}
		if (!(lastx == x && lasty == y - 1)) {
			markKilled(x, y, x, y - 1);
		}
		if (!(lastx == x + 1 && lasty == y)) {
			markKilled(x, y, x + 1, y);
		}
		if (!(lastx == x - 1 && lasty == y)) {
			markKilled(x, y, x - 1, y);
		}
		markKilled(x, y, x + 1, y + 1);
		markKilled(x, y, x - 1, y + 1);
		markKilled(x, y, x + 1, y - 1);
		markKilled(x, y, x - 1, y - 1);
	}
	
	public boolean isSetup() {

		if (hasDiagonals())
			return false;
		if (hasRightShips())
			return true;
		return false;

	}

	public boolean hasDiagonals() {

		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] == 2) {

					if (inBounds(r - 1, c - 1))
						if (board[r - 1][c - 1] == 2)
							return true;

					if (inBounds(r - 1, c + 1))
						if (board[r - 1][c + 1] == 2)
							return true;

					if (inBounds(r + 1, c - 1))
						if (board[r + 1][c - 1] == 2)
							return true;

					if (inBounds(r + 1, c + 1))
						if (board[r + 1][c + 1] == 2)
							return true;

				}
			}
		}
		return false;
	}

	public boolean hasRightShips() {

		Ships ships = new Ships();
		Ship temp = null;
		boolean hasTemp = false;

		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {

				if (board[r][c] == 2) {

					if (inBounds(r - 1, c) && ships.find(new Point(r - 1, c)) != null) {
						Ship s = ships.find(new Point(r - 1, c));
						s.getPoints().add(new Point(r, c));

					} else if (hasTemp) {
						temp.getPoints().add(new Point(r, c));

					} else {
						Ship s = new Ship();
						s.getPoints().add(new Point(r, c));
						ships.add(s);
						temp = s;
						hasTemp = true;
					}
				} else {
					hasTemp = false;
				}
			}
			hasTemp = false;
		}

		int[] lengths = new int[5];
		for (Ship s : ships) {
			lengths[s.getPoints().size()]++;
		}
		if (lengths[1] == ONES &&
				lengths[2] == TWOS &&
				lengths[3] == THREES &&
				lengths[4] == FOURS)
			return true;

		return false;
	}

	public String toJSON() {
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		for (int[] i : board) {
			JSONArray row = new JSONArray();
			for (int a : i) {

				row.add(a);
			}
			arr.add(row);
		}
		obj.put("board", arr);
		return obj.toString();
	}

    public boolean gameOver()
    {
        for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[r].length; c++)
            {
                if(board[r][c] == 2)
                {
                    return false;
                }                                                       
            }
        }
        return true;
    }
	
	public boolean inBounds(int x, int y) {
		if (x >= board.length || y >= board[0].length || x < 0 || y < 0) {
			return false;
		}
		return true;
	}

	public String mark(int x, int y, int value) {
		board[x][y] = value;
		return "marked";
	}

	public int getPos(int x, int y) {
		return board[x][y];
	}

	private class Ships extends ArrayList<Ship> {

		public Ship find(Point p) {
			for (Ship s : this) {
				if (s.getPoints().contains(p)) {
					return s;
				}
			}
			return null;
		}

	}

	private class Ship {

		private ArrayList<Point> points = new ArrayList<Point>();

		public ArrayList<Point> getPoints() {
			return points;
		}

	}

}
