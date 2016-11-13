package  com.lte.models;

/**
 * GameDB is a model.<br>
 * GameDB-Objects are used for reconstruct the past games.<br>
 * One GameDB-Object equals one entry in DB-Table Game
 * 
 * @author Florian
 *
 */
public class GameDB {
	private int gameID;
	private String opponentName;
	private String playTime;
	private int numberOfSets;

	/**
	 * Constructor for GameDB<br><br>
	 * 
	 * @param gameID - int: Game ID (ID in DB)
	 * @param opponentName - String: name of opponent
	 * @param playTime - String: Date and Time 
	 * @param numberOfSets - int: amount of sets in this game
	 */
	public GameDB(int gameID, String opponentName, String playTime, int numberOfSets) {
		this.gameID = gameID;
		this.opponentName = opponentName;
		this.playTime = playTime;
		this.numberOfSets = numberOfSets;
	}

	//Getter and Setter
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