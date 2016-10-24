package com.lte.models;

public class DBscoreboard {
	private String opponentName;
	private int wins;
	private int loses;
	private int score;
	
	public String getOpponentName() {
		return opponentName;
	}
	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLoses() {
		return loses;
	}
	public void setLoses(int loses) {
		this.loses = loses;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
