package sharonxi_CSCI201L_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ServerThread extends Thread {

	private PrintWriter pw;
	private BufferedReader br;
	private Socket s;
	private HangmanServer hs;
	private Lock lock;
	private Condition canTalk;
	private boolean isFirst;
	private boolean inGame; 
	private boolean gameReady;
	private boolean loggedIn;
	
	
	// Added Lock and Condition so that it has access to these variables
	// Added isFirst boolean so that the first Client knows that they can talk
	// as opposed to waiting for their turn even though they're the first one to connect
	public ServerThread(Socket s, HangmanServer hs, Lock lock, Condition canTalk, boolean isFirst) {
		try {
			// Assign parameters to private variables so we can use them later
			this.s = s;
			this.hs = hs;
			this.lock = lock;
			this.canTalk = canTalk;
			this.isFirst = isFirst;
			this.inGame = false;
			this.gameReady = false;
			this.loggedIn = false;
			
			pw = new PrintWriter(s.getOutputStream());
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}

	public void sendMessage(String message) {
		pw.println(message);
		pw.flush();
	}
	
	public void run() {
		try {
			String curruser = null;
			String line = null;
			int numplayers = 0;
			// STARTING AND JOINING GAMES ========================================================
			// Wait for people to make or join game
			while(!gameReady) {
				line = br.readLine();
				// If client wrote CURRUSER, read in curruser and send HangmanServer
				if(line.contains("CURRUSER")) {
					// Next line will be curruser
					curruser = br.readLine();
					// Add into HangmanServer
					hs.getUser(curruser);
				}
				// If client wrote STARTGAME, have HangmanServer start a game 
				// With the current user as the owner
				// Update the ServerThread's inGame
				if(line.contains("STARTGAME")) {
					this.inGame = true;
					// Next line will be curruser
					curruser = br.readLine();
					// Next line will be game name
					String gamename = br.readLine();
					// Next line will be numplayers
					numplayers = Integer.parseInt(br.readLine());
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					System.out.print(hs.sdf.format(timestamp) + " ");
					System.out.println(curruser + " - successfully started game " + gamename + ".");
					System.out.print(hs.sdf.format(timestamp) + " ");
					System.out.println(curruser + " - " + gamename + " needs " + numplayers + " players to start the game.");
					hs.startGame(curruser, gamename, numplayers);
					gameReady = true;
				}
				// If client wrote JOINGAME, check HangmanServer for an existing game
				if(line.contains("JOINGAME")) {
					boolean joinedGame = false;
					// Next line will be curruser
					curruser = br.readLine();
					// Next line will be gamename
					String gamename = br.readLine();
					// Try to join game. 
					String tryjoin = hs.joinGame(curruser,gamename);
					// If tryjoin is null, join was successful
					if(tryjoin == null) {
						this.inGame = true;
						sendMessage("joined successfully!");
						joinedGame = true;
						gameReady = true;
					}
					else {
						sendMessage(tryjoin);
					}
					while(!joinedGame) {
						sendMessage("\n" + "What is the name of the game?");
						gamename = br.readLine();
						// Next line will be game name
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						System.out.print(hs.sdf.format(timestamp) + " ");
						System.out.println(curruser + " - wants to join game " + gamename + ".");
						// Try to join game. 
						tryjoin = hs.joinGame(curruser,gamename);
						// If tryjoin is null, join was successful
						if(tryjoin == null) {
							this.inGame = true;
							sendMessage("joined successfully!");
							joinedGame = true;
							gameReady = true;
						}
						else {
							sendMessage(tryjoin);
						}
					}
				}
			}
			// Check if all players joined
			boolean allJoined = false;
			while(!allJoined) {
				String status = hs.checkStatus();
				if(status.contains("All")) {
					allJoined = true;
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					System.out.print(hs.sdf.format(timestamp) + " ");
					System.out.println(curruser + " - " + hs.Game.gameName + " has " + numplayers + " players so starting game.");
				}
				sendMessage(status);
			}
			// INITIALIZE GAME ===============================================================
			// Save game
			HangmanGame game = hs.Game;
			// Find a secret word
			sendMessage("\n" + "Determining secret word...");
			String secretword = hs.getSecretWord();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.print(hs.sdf.format(timestamp) + " ");
			System.out.println(curruser + " - Secret word is " + secretword + ".");
			String guess = "";
			int won = -1;
			// ENTER GAME LOOP ================================================================
			while(gameReady) {
				if(game.allGuessed()) {
					sendMessage("You guessed all correctly! You win!");
					won = 1;
					break;
				}
				if(game.guesses==0) {
					sendMessage("\n" + "You used up all your guesses! You lose!");
					won = -1;
					break;
				}
				
				sendMessage("\n" + "Secret Word: " + game.getSeen());
				sendMessage("\n" + "You have " + game.guesses + " incorrect guesses left.");
				sendMessage("\t 1) Guess a Letter");
				sendMessage("\t 2) Guess a Word");
				sendMessage("\n" + "What would you like to do?");
				String answer = br.readLine();
				if(answer.contains("1")) {
					// User wants to guess a letter
					sendMessage("\n" + "Letter to guess - ");
					guess = br.readLine();
					timestamp = new Timestamp(System.currentTimeMillis());
					System.out.print(hs.sdf.format(timestamp) + " ");
					System.out.println(hs.Game.gameName + " " + curruser + " guessed letter " + guess + ".");
					// Get positions where guessed letter is in secretword
					Vector<Integer> pos = game.guessLetter(guess);
					// If pos is empty, the guess was wrong
					if(pos.isEmpty()) {
						sendMessage("The letter '" + guess + "' is not in the secret word.");
						timestamp = new Timestamp(System.currentTimeMillis());
						System.out.print(hs.sdf.format(timestamp) + " ");
						System.out.println(hs.Game.gameName + " " + curruser + " - " + guess + " is not in " + secretword + ".");
						System.out.println(hs.Game.gameName + " now has " + hs.Game.guesses + " guesses remaining.");
					}
					else {
						sendMessage("The letter '" + guess + "' is in the secret word.");
						timestamp = new Timestamp(System.currentTimeMillis());
						System.out.print(hs.sdf.format(timestamp) + " ");
						System.out.print(hs.Game.gameName + " " + curruser + " - " + guess + " is in " + secretword + " in position(s) ");
						for(int i=0; i<pos.size(); i++) {
							if(i+1 == pos.size()) {
								System.out.println(pos.get(i) + ". Secret word now shows " + hs.Game.getSeen() + ".");
							}
							else {
								System.out.print(pos.get(i) + ", ");
							}
						}
					}
				}
				else if(answer.contains("2")) {
					// User wants to guess a word
					// This will exit the user from the game, regardless of if they are right or wrong
					sendMessage("\n" + "What is the secret word?");
					guess = br.readLine();
					timestamp = new Timestamp(System.currentTimeMillis());
					System.out.print(hs.sdf.format(timestamp) + " ");
					System.out.println(hs.Game.gameName + " " + curruser + " - guessed the word " + guess + ".");
					if(guess.contentEquals(secretword)) {
						sendMessage("\n" + "That is correct! You win!");
						timestamp = new Timestamp(System.currentTimeMillis());
						System.out.print(hs.sdf.format(timestamp) + " ");
						System.out.println(hs.Game.gameName + " " + curruser + " - " + guess + " is correct.");
						System.out.print(curruser + " wins the game. ");
						for(int i=0; i<hs.Game.players.size(); i++) {
							System.out.print(hs.Game.players.get(i) + ", ");
						}
						System.out.println(" have lost the game.");
						won = 1;
					}
					else {
						sendMessage("\n" + "That is incorrect! You lose!");
						sendMessage("The word was \"" + secretword + "\".");
						timestamp = new Timestamp(System.currentTimeMillis());
						System.out.print(hs.sdf.format(timestamp) + " ");
						System.out.println(hs.Game.gameName + " " + curruser + " - " + guess + " is incorrect.");
						System.out.println(curruser + " has lost and is no longer in the game.");
						won = -1;
					}
					gameReady = false;
				}
				else {
					sendMessage("\n" + "That is not a valid option.");
				}
			}
			// Exited game loop. Print user stats
			// Update stats depending on if user won or lost
			hs.userResult(curruser,won);
			Vector<Integer> stats = hs.userStats(curruser);
			sendMessage("\n" + curruser + "'s Record");
			sendMessage("-------------");
			sendMessage("Wins - " + stats.get(0));
			sendMessage("Losses - " + stats.get(1));
		} 
		catch (IOException ioe) {
			System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		catch (NullPointerException npe) {
			System.out.println("npe in ServerThread.run(): " + npe.getMessage());
		}
		finally {
			// If there is ever an exception thrown, release lock to prevent any deadlocks
			lock.unlock();
		}
		
		sendMessage("Thank you for playing Hangman!");
	}
}
