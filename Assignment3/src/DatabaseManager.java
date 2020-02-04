package sharonxi_CSCI201L_Assignment3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
	/*
	 * Returns int userID from table BookUsers when given 
	 * a username. Returns 0 if user doesn't exist.
	 */
	public static int getUserID(String user) {
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int uID = 0;
						
		// SQL query gets all rows from UserLogin
		String searchString = "SELECT * FROM UserLogin WHERE username = ?";
						
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setString(1, user);
			rs = ps.executeQuery();
							
			// If we get a non-empty set, the user exists
			if(rs.next()) {
				uID = rs.getInt("userID");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return uID;
	}
	/*
	 * Check if user exists in the database. Returns true if 
	 * the user exists, returns false otherwise. 
	 */
	public static boolean checkUser(String user) {
		System.out.println("inside DatabaseManager checkUser");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exists = false;
				
		// SQL query gets all rows from UserLogin
		String searchString = "SELECT * FROM UserLogin WHERE username = ?";
				
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setString(1, user);
			rs = ps.executeQuery();
					
			// If we get a non-empty set, the user exists
			if(rs.next()) {
				exists = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return exists;
	}
	/*
	 * Check if password matches the username. Returns true if
	 * it is a match, returns false otherwise. 
	 */
	public static boolean checkLogin(String user, String pass) {
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean valid = false;
		
		// SQL query gets all rows from UserLogin
		String searchString = "SELECT * FROM UserLogin WHERE username = ? AND password = ?";
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setString(1, user);
			ps.setString(2, pass);
			rs = ps.executeQuery();
			
			// If we get a non-empty set, login credentials are valid
			if(rs.next()) {
				valid = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		
		return valid;
	}
	/*
	 * Attempts to register a new user. First checks if username
	 * already exists, and if so returns false. If username doesn't 
	 * exist, then adds the user to the database.
	 */
	public static boolean newUser(String user, String pass) {
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean added = false;
		
		// SQL queries
		// Gets all rows from UserLogin
		String searchString = "SELECT * FROM UserLogin WHERE username = ?";
		
		// Creates new entry
		String newEntryString = "INSERT INTO UserLogin (username, password) VALUES (?,?)";
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setString(1,user);
			rs = ps.executeQuery();
			
			// if resultSet is empty, then the username doesn't exist
			// add the username and password into UserLogin table
			if(!rs.next()) {
				if(ps != null) {
					ps.close();
				}
				ps = conn.prepareStatement(newEntryString);
				ps.setString(1,user);
				ps.setString(2, pass);
				ps.executeUpdate();
				// make sure to return true after adding in new entry
				added = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return added;
	}
	/* 
	 * Adds a book to a user's favorites in table UserFaves
	 * by id number, for the sake of easy search later
	 * when displaying favorites. Returns false if id already
	 * exists as that user's favorite, returns true if 
	 * book is successfully added as the user's favorite. 
	*/
	public static boolean addFave(int uID, String bID) {
		System.out.println("inside DatabaseManager.addFave");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean added = false;
		
		// SQL queries
		// Checks if book already exists in this user's favorites
		String searchString = "SELECT * FROM UserFaves WHERE userID = ? AND bookID = ?";
		
		// Creates new entry in UserFaves
		String newEntryString = "INSERT INTO UserFaves (userID, bookID) VALUES (?,?)";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setInt(1,uID);
			ps.setString(2,bID);
			rs = ps.executeQuery();
			
			// if resultSet is empty, this book has not been 
			// added as this user's favorite yet. Add it in. 
			if(!rs.next()) {
				if(ps != null) {
					ps.close();
				}
				ps = conn.prepareStatement(newEntryString);
				ps.setInt(1,uID);
				ps.setString(2,bID);
				ps.executeUpdate();
				// make sure to return true after adding in new entry
				added = true;
				System.out.println("successfully added fave!");
			}
			else {
				System.out.println("can't add fave for some reason");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return added;
	}
	/* 
	 * Removes a book from a user's favorites in table UserFaves
	 * by id number. Returns false if id does not
	 * exist in that user's favorites, returns true if 
	 * book is successfully removed from the user's favorites. 
	*/
	public static boolean removeFave(int uID, String bID) {
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean removed = false;
		
		// SQL queries
		// Checks if book already exists in this user's favorites
		String searchString = "SELECT * FROM UserFaves (userID, bookID) WHERE userID = ? AND bookID = ?";
		
		// Deletes an entry in UserFaves
		String deleteEntryString = "DELETE FROM UserFaves WHERE userID = ? AND bookID = ?";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setInt(1,uID);
			ps.setString(2,bID);
			rs = ps.executeQuery();
			
			// if resultSet is not empty, this book is confirmed to be in user's 
			// favorites. Remove the book from user's favorite.  
			if(rs.next()) {
				if(ps != null) {
					ps.close();
				}
				ps = conn.prepareStatement(deleteEntryString);
				ps.setInt(1,uID);
				ps.setString(2,bID);
				ps.executeUpdate();
				// make sure to return true after deleting the entry
				removed = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return removed;
	}
	/* 
	 * Checks if there exists the book in the user's favorites. 
	 * If so, return true. If not, return false. 
	 */
	public static boolean isFave(int uID, String bID) {
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean isfave = false;
				
		// SQL queries
		// Checks if book already exists in this user's favorites
		String searchString = "SELECT * FROM UserFaves (userID, bookID) WHERE userID = ? AND bookID = ?";
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://google/BookUsers?cloudSqlInstance=csci201-assignment3-257904"
					+ ":us-central1:csci201hw3&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
					+ "&user=sharonxi&password=root");
			ps = conn.prepareStatement(searchString);
			ps.setInt(1,uID);
			ps.setString(2,bID);
			rs = ps.executeQuery();
					
			// if resultSet is not empty, this book is confirmed to be in user's 
			// favorites. isFave is true. 
			if(rs.next()) {
				isfave = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Close connections
			try {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return isfave;
	}
}