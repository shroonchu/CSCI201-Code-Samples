package sharonxi_CSCI201L_Assignment3;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class FormServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside LoginServlet");
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String user = request.getParameter("username");
		String pass = request.getParameter("password");
		// If either username or password box is left empty, print error message and return to login page
		if(user=="" || user==null || pass=="" || pass==null){
			String emptyform = "One or more entries are empty.";
			request.setAttribute("emptyform", emptyform);
			RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
			rd.forward(request,response);
		}
		// If user doesn't exist, print error message and return to login page
		else if(!DatabaseManager.checkUser(user)){
			String baduser = "This user does not exist.";
			request.setAttribute("baduser", baduser);
			RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
			rd.forward(request,response);
		}
		// We now know user exists. If password is wrong, print error message and return to login page
		else if(!DatabaseManager.checkLogin(user,pass)){
			String badpass = "Incorrect password.";
			request.setAttribute("badpass", badpass);
			RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
			rd.forward(request,response);
		}
		// We now know the username and password were both correct. Send the user to home page.
		// Send the username. 
		else{
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			Cookie userName = new Cookie("user", user);
			response.addCookie(userName);
			response.sendRedirect("HomePage.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
