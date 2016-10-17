package  com.lte.models;

public class GameDB {
	private int gameID;
	private int opponentID;
	private String playTime;
	private int pointsOpponent;
	private int pointsOwn;
	private String winner;
	
	
	public GameDB(int gameID, int opponentID, String playTime, int pointsOwn, int pointsOpponent, String winner) {
		this.gameID = gameID;
		this.opponentID = opponentID;
		this.playTime = playTime;
		this.pointsOwn = pointsOwn;
		this.pointsOpponent = pointsOpponent;
		this.winner = winner;
	}
	
	public int getGameID() {
		return gameID;
	}
	public int getOpponentID() {
		return opponentID;
	}
	public String getPlayTime() {
		return playTime;
	}
	public int getPointsOpponent() {
		return pointsOpponent;
	}
	public int getPointsOwn() {
		return pointsOwn;
	}
	public String getWinner() {
		return winner;
	}
	
	
}
