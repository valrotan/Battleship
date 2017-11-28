package battleships;

public class PlayingGame extends Game {

	public PlayingGame(Game g) {
		super(g);

	}

	@Override
	public String click(int playerNum, int x, int y) {

		if (playerNum != super.getPlayerTurn())
			return "notYourTurn";

		if (playerNum == 0) {
			String s = super.getPlayer1().attack(x, y);
			int mark = super.getPlayer1().getPos(x, y);
			super.getPlayer0Enemy().mark(x, y, mark);

			if (s.equals("hit") || s.equals("killed"))
				super.setPlayerTurn(0);
			else if (!(s.equals("couldNotAttack")))
				super.setPlayerTurn(1);
			if (s.equals("killed")) {
				super.getPlayer0Enemy().markKilled(x, y, x, y);
			}
			return s;

		} else if (playerNum == 1) {
			String s = super.getPlayer0().attack(x, y);
			int mark = super.getPlayer0().getPos(x, y);
			super.getPlayer1Enemy().mark(x, y, mark);

			if (s.equals("hit") || s.equals("killed"))
				super.setPlayerTurn(1);
			else if (!(s.equals("couldNotAttack")))
				super.setPlayerTurn(0);
			if (s.equals("killed")) {
				super.getPlayer1Enemy().markKilled(x, y, x, y);
			}
			return s;
		}

		return "Failed in Playing Game";
	}

	@Override
	public String getGameStatus(int playerNum) {

		if (playerNum == 0)
			if (super.getPlayer0().gameOver())
				return "youLost";
			else if (super.getPlayer1().gameOver())
				return "youWon";
			else
				return "keepPlaying";
		else if (super.getPlayer1().gameOver())
			return "youLost";
		else if (super.getPlayer0().gameOver())
			return "youWon";
		else
			return "keepPlaying";
	}

}
