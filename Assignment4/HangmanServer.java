package sharonxi_CSCI201L_Assignment4;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.locks.*;

public class HangmanServer {
	private Vector<Lock> locks;
	private Vector<Condition> conditions;
	private Vector<ServerThread> serverThreads;
	private Vector<String> currentUsers;
	public HangmanGame Game;
	private SQL_Util sql;
	public Vector<String> secretWords;
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	public static final Random rand = new Random();
	
	// Keep track of who can talk
	private int currentClient=0;
	
	public HangmanServer(int port, SQL_Util sql) {
		try {
			// Bind to port
			//System.out.println("Binding to port " + port);
			ServerSocket ss = new ServerSocket(port);
			//System.out.println("Bound to port " + port);
			
			// Instantiate private variables
			locks = new Vector<Lock>();
			conditions = new Vector<Condition>();
			serverThreads = new Vector<ServerThread>();
			currentUsers = new Vector<String>();
			Game = null;
			this.sql = sql;
			secretWords = new Vector<String>();
			
			// Add in secretWords
			String wordfilename = Configuration.ConfigProps.getProperty("SecretWordFile");
			File file = new File(wordfilename); 
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null) {
				if(str != null) {
					secretWords.add(str);
				}
			}
			br.close();
			
			// Start listening for connections
			while(true) {
				Socket s = ss.accept(); // blocking
				System.out.println("Connection from: " + s.getInetAddress());
				Lock lock = new ReentrantLock();
				Condition canTalk = lock.newCondition();
				
				// serverThreads.isEmpty() tells us if there are servers there already in our list or not
				// If there are none, then this new server thread is the first one to connect
				ServerThread st = new ServerThread(s, this, lock, canTalk, serverThreads.isEmpty());
				InputStream is = s.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                str = null;
                if((str = br.readLine()) != null) {
                	String curruser = str;
                    this.getUser(curruser);
                }
				// Add relevant variables to their respective vectors
				locks.add(lock);
				conditions.add(canTalk);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in HangmanServer constructor: " + ioe.getMessage());
		}
	}
	
	public void broadcast(String message, ServerThread st) {
		if (message != null) {
			System.out.println(message);
			for(ServerThread threads : serverThreads) {
				if (st != threads) {
					threads.sendMessage(message);
				}
			}
		}
	}
	
	// Saves users that are connected into the vector
	public void getUser(String curruser) {
		if (curruser != null) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.print(sdf.format(timestamp) + " ");
			System.out.println(curruser + " - successfully logged in.");
			currentUsers.add(curruser);
		}
	}
	
	// Begins game
	public void startGame(String curruser, String gamename, int numplayers) {
		// If there is another game going on, don't create a new game
		if(this.Game != null) {
			return;
		}
		Game = new HangmanGame(curruser, gamename, numplayers);
		
	}
	
	// Try to join curruser to the given gamename. Returns error messages if 
	// join unsuccessful. Returns null if successful.
	public String joinGame(String curruser, String gamename) {
		// Check if game exists
		if(!gamename.contentEquals(this.Game.gameName)) {
			String result = "There is no game with name " + gamename + ".";
			return result;
		}
		// If game exists, check if needs more players
		else if(this.Game.numplayers==0) {
			String result = "The game " + gamename + " does not have space for another user to join.";
			return result;
		}
		// Otherwise, join player into game
		else {
			this.Game.addPlayer(curruser);
			return null;
		}
	}
	
	// Returns String describing the current waiting status of game
	// Basically how many players the game is waiting for.
	public String checkStatus() {
		int num = this.Game.numplayers;
		if(num == 0) {
			return "All users have joined.";
		}
		else if(num == 1){
			return "Waiting for " + num + " other user to join...";
		}
		else {
			return "Waiting for " + num + " other users to join...";
		}
	}
	
	// Update player stats if won (r=1) or lost (r=-1)
	public void userResult(String username, int r) {
		this.sql.updateStats(username,r);
	}
	
	// Returns a vector of a given player's stats
	public Vector<Integer> userStats(String username){
		return this.sql.getStats(username);
	}

	// Returns a string of the determined secret word
	// Initializes game with secret word
	public String getSecretWord() {
		if(this.Game.secretWord != null) {
			return this.Game.secretWord;
		}
		else {
			int r = rand.nextInt(this.secretWords.size());
			String secretword = this.secretWords.get(r);
			this.Game.initWord(secretword);
			return secretword;
		}
	}
	
	public static void main(String [] args) {
		boolean foundfile = false;
		Scanner serverscan = new Scanner(System.in);
		// Keep asking user for a configuration file name until one works
		while(!foundfile) {
			System.out.print("Enter name of configuration file: ");
			String filename = serverscan.nextLine();
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

		// Try to make database connection
		System.out.print("Trying to connect to database...");
		String conn = null;
		String user = null;
		String password = null;
		SQL_Util sql = null;
		try {
			conn = Configuration.ConfigProps.getProperty("DBConnection");
			user = Configuration.ConfigProps.getProperty("DBUsername");
			password = Configuration.ConfigProps.getProperty("DBPassword");
			sql = new SQL_Util(conn, user, password);
		}
		catch(SQLException sqle) {
			int qm = conn.indexOf('?');
			System.out.println("Unable to connect to database " + conn.substring(0,qm+1) + "... with username "
							+ user + " and password " + password + ".");
			serverscan.close();
			return;
		}
		// If past the catch block, then the connection was successful
		System.out.println("Connected!");
		
		// Connect HangmanServer with port
		int port = 0;
		try {
			port = Integer.parseInt(Configuration.ConfigProps.getProperty("ServerPort"));
		}
		catch(NumberFormatException nfe) {
			System.out.println("nfe in HangmanServer.main(): " + nfe.getMessage());
		}
		HangmanServer hs = new HangmanServer(port, sql);
		serverscan.close();
	}
}
