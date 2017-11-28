package battleships;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class RequestHandler
 */
public class RequestHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static int nextId;

	private static ArrayList<GameRoom> gameRooms = new ArrayList<GameRoom>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();

		if (request.getParameterMap().containsKey("q")) {

			String req = request.getParameter("q");
			
			// refresh the session on page refresh/login
			if (req.contains("newSession")) {

				if (!session.isNew())
					session.invalidate();
				session = request.getSession();
				out.println("New Session Created Successfully");
				session.setAttribute("board", new Board());
				return;

			// attempt to join a room
			} else if (req.startsWith("joinRoom")) {
				// check if board is set up
				Board b = (Board) session.getAttribute("board");
				if (b == null) {
					out.write("noBoardFound");
					return;
				}
				if (!b.isSetup()) {
					out.write("invalidBoardSetUp");
					return;
				}
				
				// make sure client did not join a room already
				if (session.getAttribute("gameId") == null) {
					String id = req.substring(8);
					// join given room
					if (id.length() == 4) {
						join (request, id);
					} else {
						join(request);
					}
				}
				
				// return the gameId and the player number if joined successfully
				if (session.getAttribute("gameId") != null) {
					out.write(session.getAttribute("gameId") + "" + session.getAttribute("playerNumber"));
				} else {
					out.write("roomNotFound");
					return;
				}

				// set new board equal to previous
				find(session.getAttribute("gameId") + "").setBoard(Integer.parseInt(session.getAttribute("playerNumber").toString()), b);
			}
			if (req.equals("getGameStatus")) {
				if (session.getAttribute("gameId") == null) {
					out.write("placingSingle");
				}
			}
		}
		if (request.getParameterMap().containsKey("click")) {
			if (session.getAttribute("gameId") == null) {
				
				Board b = (Board) session.getAttribute("board");
				if (b != null) {
					
					String click = request.getParameter("click");
					int x = Integer.parseInt(click.substring(0, click.indexOf(',')));
					int y = Integer.parseInt(click.substring(click.indexOf(',') + 1));
					response.getWriter()
							.write(b.place(x, y));
				}
			}
		}
		if (session.getAttribute("gameId") != null) {
			find(session.getAttribute("gameId") + "").doGet(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// forward the request to the individual more specialized game room of the player if the player joined a room
		if (request.getSession().getAttribute("gameId") != null) {
			find(request.getSession().getAttribute("gameId") + "").doPost(request, response);
		} else {
			
			String body = request.getReader().readLine();
			Board b = null;
			if (body.equals("getHomeBoard")) {
				b = (Board) request.getSession().getAttribute("board");
				response.getWriter().write(b.toJSON());	
			}		
			
		}

	}

	public boolean join(HttpServletRequest request, String id) {
		
		// find the entered game room and join the player if possible
		GameRoom gr = find(id);
		if (gr != null && gr.getPlayerNum() != 2) {
			request.getSession().setAttribute("gameId", gr.getId());
			request.getSession().setAttribute("playerNumber", gr.getPlayerNum() + "");
			gr.addPlayer();
			return true;
		}
		return false;
		
	}

	// joins the player to next available room; if no room available, creates a
	// new room and joins it
	public boolean join(HttpServletRequest request) {

		for (GameRoom gr : gameRooms) {
			if (gr.getPlayerNum() < 2) {
				request.getSession().setAttribute("gameId", gr.getId());
				request.getSession().setAttribute("playerNumber", gr.getPlayerNum() + "");
				gr.addPlayer();
				return true;
			}
		}
		String formatted = String.format("%04d", nextId);
		gameRooms.add(new GameRoom(formatted + ""));
		incrementId();
		return join(request);
	}

	public GameRoom find(String id) {

		for (GameRoom gr : gameRooms) {
			if (gr.getId().equals(id)) {
				return gr;
			}
		}
		return null;
	}

	private void incrementId() {

		nextId++;
		if (nextId >= Math.pow(10, 4)) {
			nextId -= Math.pow(10, 4);
		}
	}

}
