package sharonxi_CSCI201L_Assignment4;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class HangmanClient extends Thread {
	private BufferedReader br;
	private static PrintWriter pw;
	static Scanner scan = new Scanner(System.in);
	public static String curruser = null;
	private static Socket socket;
	
	public HangmanClient(String hostname, int port) throws IOException, NullPointerException {
		try {
			System.out.print("Trying to connect to server...");
			socket = new Socket(hostname, port);
			System.out.println("Connected!");
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream());
			this.start();
			/*while(true) {
				String line = scan.nextLine();
				pw.println("Donald: " + line);
				pw.flush();
			}*/
			
		} catch (IOException ioe) {
			System.out.println("Unable to connect to server " + hostname + " on port " + port + ".");
			socket.close();
			throw new IOException();
		}
	}
	public void run() {
		try {
			while(true) {
				String line = br.readLine();
				System.out.println(line);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
		}
	}
	public static void main(String [] args) {
		boolean foundfile = false;
		// CONFIGURATION FILE =================================================================
		// Keep asking user for a configuration file name until one works
		while(!foundfile) {
			System.out.print("Enter name of configuration file: ");
			String filename = scan.nextLine();
			System.out.println("Reading config file...");
			foundfile = Configuration.findConfigFile(filename);
			if(!foundfile) {
				System.out.println("Configuration file " + filename + " could not be found.");
			}
			else if(foundfile) {
				System.out.println(Configuration.readConfigFile());
				if(!Configuration.validfile) {
					// If the file wasn't valid, make sure to go back into the loop
					foundfile = false;
				}
			}
		}
		// CONNECT TO SERVER ==================================================================
		int port = 0;
		String hostname = null;
		try {
			port = Integer.parseInt(Configuration.ConfigProps.getProperty("ServerPort"));
			hostname = Configuration.ConfigProps.getProperty("ServerHostname");
		}
		catch(NumberFormatException nfe) {
			System.out.println("nfe in ChatClient.main(): " + nfe.getMessage());
		}
		// Try to connect to the server
		try {
			HangmanClient hc = new HangmanClient(hostname, port);
		}
		catch(IOException ioe) {
			return;
		}
		catch(NullPointerException npe){
			return;
		}
		try {
			// Establish connection
			String conn = Configuration.ConfigProps.getProperty("DBConnection");
			String user = Configuration.ConfigProps.getProperty("DBUsername");
			String pass = Configuration.ConfigProps.getProperty("DBPassword");
			SQL_Util sql = new SQL_Util(conn,user,pass);
			// LOGIN LOOP ===================================================================
			String username = null;
			String password = null;
			String answer = null;
			boolean loggedin = false;
			// While user has not successfully logged in, keep prompting
			// user chances to log in
			while(!loggedin) {
				// Read in a username and password
				System.out.println("");
				System.out.print("Username: ");
				username = scan.nextLine();
				System.out.print("Password: ");
				password = scan.nextLine();
				System.out.println("");
				// Check if login credentials are valid
				boolean validuser = sql.checkUser(username);
				boolean validpassword = sql.checkLogin(username, password);
				// If either username or password are wrong, 
				// ask if user would like to create new account
				if(!validuser || !validpassword) {
					System.out.println("No account exists with those credentials.");
					System.out.println("Would you like to create a new account?");
					answer = scan.nextLine();
					// If "Yes", ask if user wants to use above credentials
					if(answer.contentEquals("Yes")) {
						boolean added = false;
						System.out.println("Would you like to use the username and password above?");
						answer = scan.nextLine();
						// If not "Yes", ask for new credentials
						if(!answer.contentEquals("Yes")) {
							System.out.println("Please enter your new account credentials.");
							System.out.print("Username: ");
							username = scan.nextLine();
							System.out.print("Password: ");
							password = scan.nextLine();
							System.out.println("");
						}
						// Try to make new user.
						if(!sql.addUser(username, password)) {
							System.out.println("That username already exists. Sending you back to login.");
						}
						// Otherwise the user has been created
						else {
							System.out.println("Great! You are now logged in as " + username + ".");
							System.out.println("");
							loggedin = true;
							validuser = true;
							validpassword = true;
							curruser = username;
						}
					}
					// If user does not want to make new account, continue in loop
				}
				// If credentials are valid, get out of login loop
				else {
					System.out.println("Great! You are now logged in as " + username + ".");
					System.out.println("");
					loggedin = true;
					curruser = username;
				}
			}
			// User successfully logged in. Pass username to server and print out their stats.
			pw.println(curruser);
			pw.flush();
			Vector<Integer> stats = sql.getStats(curruser);
			System.out.println(curruser + "'s Record");
			System.out.println("------------------");
			System.out.println("Wins - " + stats.get(0));
			System.out.println("Losses - " + stats.get(1));
			
			// STARTING AND JOINING GAMES ========================================================
			// Ask user to start or join game
			System.out.println("\n \t 1) Start a Game");
			System.out.println("\t 2) Join a Game");
			int startjoin = Integer.parseInt(scan.nextLine().trim());
			boolean inGame = true;
			boolean joinedGame = false;
			// Signal to server that we are starting or joining a game
			// If starting a game, give server parameters to create a new game
			if(startjoin==1) {
				System.out.println("\n" + "What is the name of the game?");
				String gamename = scan.nextLine();
				pw.println("STARTGAME");
				pw.println(curruser);
				pw.println(gamename);
				pw.flush();
				System.out.println("\n" + "How many users will be playing (1-4)?");
				String numplayers = scan.nextLine();
				while(!numplayers.contentEquals("1") && !numplayers.contentEquals("2")
						&& !numplayers.contentEquals("3") && !numplayers.contentEquals("4")) {
					System.out.println("A game can only have between 1-4 players.");
					System.out.println("\n" + "How many users will be playing (1-4)?");
					numplayers = scan.nextLine();
				}
				// Signal to server how many players for this game
				pw.println(numplayers);
				pw.flush();
			}
			// If joining a game, let server check if given gamename exists
			else {
				pw.println("JOINGAME");
				pw.println(curruser);
				System.out.println("\n" + "What is the name of the game?");
				String gamename = scan.nextLine();
				pw.println(gamename);
				pw.flush();
			}
			while(inGame) {
				String line = scan.nextLine();
				pw.println(line);
				pw.flush();
			}
			
		}
		catch(SQLException sqle) {
			System.out.println(sqle.getMessage());
			return;
		}
		catch(NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
			return;
		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
// catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		scan.close();
	}
}
