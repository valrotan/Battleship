package battleships;

import java.io.IOException;

import javax.servlet.http.*;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

public class GameRoom {

	private String id;
	private int playerNum;

	private Game game;
	
	private int numSetup;
	private int playerSetup;

	// constructor for game room with id
	public GameRoom(String i) {

		id = i;
		game = new SettingGame();
		
		numSetup = 0;
		playerSetup = -1;
	}

	// processes get requests
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// checks for the click parameter and clicks on the board for the player if possible
		if (request.getParameterMap().containsKey("click")) {
			if (!(game instanceof SettingGame && Integer.parseInt(request.getSession().getAttribute("playerNumber").toString()) == playerSetup)) {
			
			String click = request.getParameter("click");
			int x = Integer.parseInt(click.substring(0, click.indexOf(',')));
			int y = Integer.parseInt(click.substring(click.indexOf(',') + 1));
			response.getWriter()
					.write(game.click(Integer.parseInt(request.getSession().getAttribute("playerNumber") + ""), x, y));
			} else {
				response.getWriter().write("alreadyConfirmed");
			}
		}
		// checks for query parameter in the request
		if (request.getParameterMap().containsKey("q")) {
			// confirms player's board and finishes placing the ships for the player
			if (request.getParameter("q").contains("finishPlacing")) {
				boolean set = false;
				if (request.getSession().getAttribute("playerNumber").toString().equals("0")) {
					set = game.getPlayer0().isSetup();
				} else {
					set = game.getPlayer1().isSetup();
				}
				if (set) {
					response.getWriter().write(finishPlacing(request));
				} else {
					response.getWriter().write("invalidBoardSetUp");
				}
			}
			// returns whether or not the board is set up correctly
			if (request.getParameter("q").contains("checkSetUp")) {
				if (request.getSession().getAttribute("playerNumber").toString().equals("0")) {
					response.getWriter().write(game.getPlayer0().isSetup() + "");
				} else {
					response.getWriter().write(game.getPlayer1().isSetup() + "");
				}
			}
			// returns the game status
			if (request.getParameter("q").contains("getGameStatus")) {
				int num = Integer.parseInt(request.getSession().getAttribute("playerNumber").toString());
				if (game instanceof SettingGame && num == playerSetup) {
					response.getWriter().write("waiting");
				} else {
					response.getWriter().write("" + game.getGameStatus(num));
				}
			}
			// returns whether ot not it is the player's turn
			if (request.getParameter("q").contains("isMyTurn")) {
				int turn = game.getPlayerTurn();
				int player = Integer.parseInt(request.getSession().getAttribute("playerNumber").toString());
				if (turn == player) {
					response.getWriter().write("true");
				} else {
					response.getWriter().write("false");
				}
			}
		}
	}

	// processes post requests
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String body = request.getReader().readLine();
		Board b = null;
		// returns the player's home board in JSON form
		if (body.equals("getHomeBoard")) {
			if (request.getSession().getAttribute("playerNumber").toString().equals("0")) {
				b = game.getPlayer0();
			} else {
				b = game.getPlayer1();
			}
		// returns the player's enemy board in JSON form
		} else if (body.equals("getEnemyBoard")) {
			if (request.getSession().getAttribute("playerNumber").toString().equals("0")) {
				b = game.getPlayer0Enemy();
			} else {
				b = game.getPlayer1Enemy();
			}
		}
		response.getWriter().write(b.toJSON());
	}

	// finishes placing the ships for the player and switches to playing the game once both players confirm
	public String finishPlacing(HttpServletRequest req) {
		
		if (numSetup == 0) {
			playerSetup = Integer.parseInt(req.getSession().getAttribute("playerNumber").toString());
			numSetup++;
			return "waiting";
		}
		
		if (playerSetup == Integer.parseInt(req.getSession().getAttribute("playerNumber").toString()))
			return "waiting";
		else
			numSetup = 2;
		
		if (game instanceof SettingGame) {
			game = new PlayingGame(game);
			return "finished";
		}
		return "alreadyFinished";
	}

	public void addPlayer() {
		playerNum++;
	}

	public String getId() {
		return id;
	}

	public int getPlayerNum() {
		return playerNum;
	}
	
	// sets the board for the specific player
	public void setBoard(int playerNum, Board b){
		if (playerNum == 0) {
			game.setPlayer0(b);
		} else {
			game.setPlayer1(b);
		}
	}

}
