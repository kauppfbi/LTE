package  com.lte.models;

public class GameDB {
	private int gameID;
	private String opponentName;
	private String playTime;
	private int numberOfSets;
	
	private int setID;
	private int pointsOpponent;
	private int pointsOwn;
	private String winner;
	private int[] replayTurns;
	
	// Constructor for first dropdown - Game Selection
	public GameDB(int gameID, String opponentName, String playTime, int numberOfSets) {
		this.gameID = gameID;
		this.opponentName = opponentName;
		this.playTime = playTime;
		this.numberOfSets = numberOfSets;
	}

	// Constructor for second dropdown - Set Selection - Replay turns already included
	public GameDB(int setID, int pointsOwn, int pointsOpponent, String winner, int[] replayTurns) {
		this.setID = setID;
		this.pointsOwn = pointsOwn;
		this.pointsOpponent = pointsOpponent;
		this.winner = winner;
		this.replayTurns = replayTurns;
	}

	public int getSetID() {
		return setID;
	}

	public void setSetID(int setID) {
		this.setID = setID;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}


	public void setNumberOfSets(int numberOfSets) {
		this.numberOfSets = numberOfSets;
	}


	public int[] getReplayTurns() {
		return replayTurns;
	}


	public void setReplayTurns(int[] replayTurns) {
		this.replayTurns = replayTurns;
	}


	public int getGameID() {
		return gameID;
	}


	public void setGameID(int gameID) {
		this.gameID = gameID;
	}


	public String getOpponentName() {
		return opponentName;
	}


	public void setOpponentName(String opponentID) {
		this.opponentName = opponentName;
	}


	public String getPlayTime() {
		return playTime;
	}


	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}


	public int getPointsOpponent() {
		return pointsOpponent;
	}


	public void setPointsOpponent(int pointsOpponent) {
		this.pointsOpponent = pointsOpponent;
	}


	public int getPointsOwn() {
		return pointsOwn;
	}


	public void setPointsOwn(int pointsOwn) {
		this.pointsOwn = pointsOwn;
	}


	public String getWinner() {
		return winner;
	}


	public void setWinner(String winner) {
		this.winner = winner;
	}	
	
}
