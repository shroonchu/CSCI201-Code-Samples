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
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside RegisterServlet");
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String user = request.getParameter("username");
		String pass = request.getParameter("password");
		String confpass = request.getParameter("confirmPassword");
		// If either username or password box is left empty, print error message and return to register page
		if(user=="" || user==null || pass=="" || pass==null){
			String emptyform = "One or more entries are empty.";
			request.setAttribute("emptyform", emptyform);
			RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
			rd.forward(request,response);
		}
		// If username exists, print error message and return to register page
		else if(DatabaseManager.checkUser(user)){
			String baduser = "This username is taken.";
			request.setAttribute("baduser", baduser);
			RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
			rd.forward(request,response);
		}
		// We now know username is available. Check if passwords match.
		else if(pass.compareTo(confpass)!=0){
			String badpass = "Passwords don't match.";
			request.setAttribute("badpass", badpass);
			RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
			rd.forward(request,response);
		}
		// We now know the username is available and the passwords match.
		// Add user to the database and send user to home page. 
		else{
			DatabaseManager.newUser(user, pass);
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
