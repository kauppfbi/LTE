package com.lte.models;

/**
 * SetDB is a model<br>
 * SetDB-Objects are used to reconstruct a selected Set<br>
 * SetDB-Object equals one entry in DB-Table GameSet<br><br>
 * 
 * Info: the int[]-Array stores the playedTurns,<br>
 * int[0] == 0 -> We started the set<br>
 * int[0] == 1 -> Enemy started the set<br>
 * 
 * @author Florian
 *
 */
public class SetDB {

	private int setID;
	private int pointsOpponent;
	private int pointsOwn;
	private String winner;
	private int[] replayTurns;
	
	/**
	 * Constructor for SetDB-Object <br><br>
	 * 
	 * @param setID
	 * @param pointsOwn
	 * @param pointsOpponent
	 * @param winner
	 * @param replayTurns
	 */
	public SetDB(int setID, int pointsOwn, int pointsOpponent, String winner, int[] replayTurns) {
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

	public int[] getReplayTurns() {
		return replayTurns;
	}

	public void setReplayTurns(int[] replayTurns) {
		this.replayTurns = replayTurns;
	}
}
