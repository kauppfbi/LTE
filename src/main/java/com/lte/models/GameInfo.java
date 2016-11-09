package  com.lte.models;

/**
 * GameInfo class
 * Getter and Setter methods to get/set information
 * of the current game
 * @author FelixH
 *
 */
public class GameInfo {
	//DB ID's
	private int gameID;
	private int setID = -1; 
	private int opponentID;
	
	
	//other attributes
	private String opponentName;
	private String ownName;
	
	boolean gameInProgress;
	char nextPlayer;
	private int set;
	private int ownPoints;
	private int opponentPoints;
	private char startingPlayer;

	
	public GameInfo(String opponentName) {
		this.opponentName = opponentName;
	}

	public GameInfo(String opponentName, String ownName) {
		this.opponentName = opponentName;
		this.ownName = ownName;
	}
	
	//Getter and Setter methods
	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public int getSetID() {
		return setID;
	}

	public void setSetID(int setID) {
		this.setID = setID;
	}

	public int getOpponentID() {
		return opponentID;
	}

	public void setOpponentID(int opponentID) {
		this.opponentID = opponentID;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}


	public boolean isGameInProgress() {
		return gameInProgress;
	}


	public void setGameInProgress(boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}


	public char getNextPlayer() {
		return nextPlayer;
	}


	public void setNextPlayer(char nextPlayer) {
		this.nextPlayer = nextPlayer;
	}


	public int getSet() {
		return set;
	}


	public void setSet(int turn) {
		this.set = turn;
	}


	public int getOwnPoints() {
		return ownPoints;
	}


	public void setOwnPoints(int ownPoints) {
		this.ownPoints = ownPoints;
	}


	public int getOpponentPoints() {
		return opponentPoints;
	}


	public void setOpponentPoints(int opponentPoints) {
		this.opponentPoints = opponentPoints;
	}

	public String getOwnName() {
		return ownName;
	}

	public void setOwnName(String ownName) {
		this.ownName = ownName;
	}

	public char getStartingPlayer() {
		return startingPlayer;
	}

	public void setStartingPlayer(char startingPlayer) {
		this.startingPlayer = startingPlayer;
	} 
	
	
}
