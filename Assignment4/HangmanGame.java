package sharonxi_CSCI201L_Assignment4;

import java.util.Vector;

public class HangmanGame{
	private static String owner;
	public String gameName;
	public static Vector<String> players;
	public String secretWord;
	public int numplayers;
	public int guesses;
	public Vector<String> seenWord;
	
	// Constructor
	public HangmanGame(String curruser, String gamename, int numplayers) {
		this.owner = curruser;
		this.gameName = gamename;
		// Initialize players vector
		this.players = new Vector<String>();
		this.secretWord = null;
		this.numplayers = numplayers;
		this.guesses = 7;
		this.seenWord = new Vector<String>();
		// add curruser into players vector
		players.add(curruser);
		this.numplayers--;
	}
	
	// Add new player
	public void addPlayer(String player) {
		players.add(player);
		numplayers--;
	}
	
	// Initializes secretWord and seenWord
	public void initWord(String word) {
		this.secretWord = word.trim();
		for(int i=0; i<word.length(); i++) {
			seenWord.add("_ ");
		}
	}
	
	// Returns string from seenWord
	public String getSeen() {
		String seen = "";
		for(int i=0; i<seenWord.size(); i++) {
			seen += seenWord.get(i);
		}
		return seen;
	}
	
	// Takes in a letter guess, updates seenWord,
	// and returns a vector of positions where letter is in seenWord
	public Vector<Integer> guessLetter(String ch) {
		String sw = this.secretWord;
		ch = ch.toUpperCase();
		Vector<Integer> pos = new Vector<Integer>();
		// if ch is in secretWord, update seenWord
		if(sw.contains(ch)) {
			for(int i=0; i<sw.length(); i++) {
				// If the letter at this index of secretWord equals ch,
				// Change seenWord to show this letter
				// Add this index to vector
				if(sw.substring(i, i+1).contentEquals(ch)) {
					this.seenWord.set(i, ch + " ");
					pos.add(i);
				}
			}
		}
		// If guess is incorrect, decrease guesses
		else {
			this.guesses--;
		}
		return pos;
	}
	
	// Returns boolean if all the letters were correctly guessed
	public boolean allGuessed() {
		for(int i=0; i<this.seenWord.size(); i++) {
			String temp = this.seenWord.get(i);
			if(temp.contentEquals("_ ")) {
				return false;
			}
		}
		return true;
	}

}