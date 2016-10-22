package com.lte.models;

public class SetDB {

	private int setID;
	private int pointsOpponent;
	private int pointsOwn;
	private String winner;
	private int[] replayTurns;
	
	// Constructor for second dropdown - Set Selection - Replay turns already included
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
