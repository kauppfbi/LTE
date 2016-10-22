package  com.lte.models;

public class GameDB {
	private int gameID;
	private String opponentName;
	private String playTime;
	private int numberOfSets;
	
	// Constructor for first dropdown - Game Selection
	public GameDB(int gameID, String opponentName, String playTime, int numberOfSets) {
		this.gameID = gameID;
		this.opponentName = opponentName;
		this.playTime = playTime;
		this.numberOfSets = numberOfSets;
	}


	//Getter and Setters
	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public void setNumberOfSets(int numberOfSets) {
		this.numberOfSets = numberOfSets;
	}
}