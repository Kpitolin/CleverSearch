package controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LaunchSearchServlet
 */
@WebServlet("/LaunchSearchServlet")
public class LaunchSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private HashMap<String, String> views;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LaunchSearchServlet() {
        super();
        
        views = new HashMap<String, String>();
        views.put("search", "pagesResults.jsp");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String commandStr = request.getParameter("action");
		
		if(!commandStr.isEmpty()){
			Action action = null;
			if(commandStr.equals("search")){
				action = new SearchAction();
			}
			action.execute(request);
			
			request.getRequestDispatcher(views.get(commandStr)).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doTrace(HttpServletRequest, HttpServletResponse)
	 */
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
