package sharonxi_CSCI201L_Assignment4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class SQL_Util {
	public static String CREDENTIALS_STRING = null;
	/*
	 * Creates an instance of a SQL_Util given a DBConnection, username, and password.
	 * Throws SQLException if connection cannot be made
	 */
	SQL_Util(String connec, String user, String password) throws SQLException{
		CREDENTIALS_STRING = connec + user + "&password=" + password;
		// Try to make connection
		Connection conn = DriverManager.getConnection(CREDENTIALS_STRING);
	}
	/*
	 * Check if user exists in the database. Returns true if 
	 * the user exists, returns false otherwise. 
	 */
	public boolean checkUser(String user){
		//System.out.println("inside SQL_Util checkUser");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exists = false;
				
		// SQL query gets all rows from Users
		String searchString = "SELECT * FROM Users WHERE username = ?";
				
		try {
			conn = DriverManager.getConnection(CREDENTIALS_STRING);
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
	public boolean checkLogin(String user, String pass) {
		//System.out.println("inside SQL_Util checkLogin");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean valid = false;
		
		// SQL query gets all rows from Users
		String searchString = "SELECT * FROM Users WHERE username = ? AND password = ?";
		
		try {
			conn = DriverManager.getConnection(CREDENTIALS_STRING);
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
	public boolean addUser(String user, String pass) {
		//System.out.println("inside SQL_Util addUser");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean added = false;
		
		// SQL queries
		// Gets all rows from UserLogin
		String searchString = "SELECT * FROM Users WHERE username = ?";
		
		// Creates new entry
		String newEntryString = "INSERT INTO Users (username, password, wins, losses) VALUES (?,?,0,0)";
		
		try {
			conn = DriverManager.getConnection(CREDENTIALS_STRING);
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
	 * Increments user's wins if count is 1, increment user's losses if
	 * count is -1.
	 */
	public static boolean updateStats(String user, int count) {
		if(count!=1 && count!=-1) {
			return false;
		}
		//System.out.println("inside SQL_Util updateStats");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean updated = false;
				
		// SQL query gets all rows from Users
		String searchString = "SELECT * FROM Users WHERE username = ?";
		
		// SQL query updates win of Users for username
		String updateWin = "UPDATE Users SET wins = ? WHERE username = ?";
		
		// SQL query updates loss of Users for username
		String updateLoss = "UPDATE Users SET losses = ? WHERE username = ?";
				
		try {
			conn = DriverManager.getConnection(CREDENTIALS_STRING);
			ps = conn.prepareStatement(searchString);
			ps.setString(1, user);
			rs = ps.executeQuery();
					
			// If we get a non-empty set, the user exists
			// Update stats
			if(rs.next()) {
				if(count == 1) {
					int wins = rs.getInt("wins");
					if(ps != null) {
						ps.close();
					}
					ps = conn.prepareStatement(updateWin);
					wins++;
					ps.setInt(1,wins);
					ps.setString(2, user);
					ps.executeUpdate();
				}
				else if(count == -1) {
					int loss = rs.getInt("losses");
					if(ps != null) {
						ps.close();
					}
					ps = conn.prepareStatement(updateLoss);
					loss++;
					ps.setInt(1,loss);
					ps.setString(2, user);
					ps.executeUpdate();
				}
				// make sure to return true after updating stats
				updated = true;
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
		return updated;
	}
	/*
	 * Returns vector of ints with user's wins and losses
	 * The first integer in vector is the wins, second integer is losses.
	 */
	public Vector<Integer> getStats(String user) {
		//System.out.println("inside SQL_Util getStats");
		// Connect to database
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Vector<Integer> stats = new Vector<Integer>();
				
		// SQL query gets all rows from Users
		String searchString = "SELECT * FROM Users WHERE username = ?";
				
		try {
			conn = DriverManager.getConnection(CREDENTIALS_STRING);
			ps = conn.prepareStatement(searchString);
			ps.setString(1, user);
			rs = ps.executeQuery();
					
			// If we get a non-empty set, the user exists
			if(rs.next()) {
				stats.add((Integer)rs.getInt("wins"));
				stats.add((Integer)rs.getInt("losses"));
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
		return stats;
	}

}

