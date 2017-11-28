package battleships;

public class SettingGame extends Game {

	@Override
	public String click(int playerNum, int x, int y) {
		if (playerNum == 0) {
			return super.getPlayer0().place(x, y);
		} else if (playerNum == 1) {
			return super.getPlayer1().place(x, y);
		}
		return "Failed in Setting Game";
	}

	@Override
	public String getGameStatus(int playerNum) {
		return "placingShips";
	}
	
}
