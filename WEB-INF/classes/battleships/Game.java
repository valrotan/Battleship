package battleships;

public abstract class Game {

	private Board player0, player0Enemy, player1, player1Enemy;
	private int playerTurn;

	public Game() {
		playerTurn = 0;
		player0 = new Board();
		player0Enemy = new Board();
		player1 = new Board();
		player1Enemy = new Board();
	}
	
	public Game(Game g) {
		player0 = g.player0;
		player0Enemy = g.player0Enemy;
		player1 = g.player1;
		player1Enemy = g.player1Enemy;
		playerTurn = 0;
	}

	public abstract String click(int playerNum, int x, int y);

	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Board getPlayer0() {
		return player0;
	}

	public Board getPlayer0Enemy() {
		return player0Enemy;
	}

	public Board getPlayer1() {
		return player1;
	}

	public Board getPlayer1Enemy() {
		return player1Enemy;
	}
	
	public void setPlayer0(Board player0) {
		this.player0 = player0;
	}

	public void setPlayer1(Board player1) {
		this.player1 = player1;
	}

	public abstract String getGameStatus (int playerNum);

}
