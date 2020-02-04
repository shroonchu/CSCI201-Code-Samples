package sharonxi_CSCI201L_Assignment3;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DetailServlet
 * Use DetailServlet simply for the purpose of passing the current user.
 */
@WebServlet("/DetailServlet")
public class DetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DetailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String curruser = request.getParameter("curruser");
		String bID = request.getParameter("id");
		System.out.println("Detail.curruser: " + curruser);
		System.out.println("Detail.id: " + bID);
		if(curruser!=null) {
			int uID = DatabaseManager.getUserID(curruser);
			// If the book is in curruser's favorites, then the button we return says "Favorite" 
			// Otherwise, it will say "Remove"
			if(DatabaseManager.isFave(uID, bID)) {
				request.setAttribute("isfave", true);
			}
		}
		RequestDispatcher rd = request.getRequestDispatcher("Details.jsp");
		rd.forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
