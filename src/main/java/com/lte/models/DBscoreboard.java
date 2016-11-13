package com.lte.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class is a classic model, which holds appropriate information for the leaderboard at the starting-screen.<br>
 * One Object holds one record for the table.
 * @author kauppfbi
 *
 */
public class DBscoreboard {
	private SimpleStringProperty opponentName;
	private SimpleIntegerProperty score;
	private SimpleIntegerProperty wins;
	private SimpleIntegerProperty loses;
	
	/*
	 * empty default constructor
	 */
	public DBscoreboard(){};
	
	/**
	 * Default constructor<br>
	 * @param opponentName
	 * @param score
	 * @param wins
	 * @param loses
	 */
	public DBscoreboard(String opponentName, int score, int wins, int loses){
		this.opponentName = new SimpleStringProperty(opponentName);
		this.score = new SimpleIntegerProperty(score);
		this.wins = new SimpleIntegerProperty(wins);
		this.loses = new SimpleIntegerProperty(loses);
	}
	
	public String getOpponentName() {
		return opponentName.get();
	}
	public void setOpponentName(String opponentName) {
		this.opponentName = new SimpleStringProperty(opponentName);
	}
	public int getScore() {
		return score.get();
	}
	public void setScore(int score) {
		this.score = new SimpleIntegerProperty(score);
	}
	public int getWins() {
		return wins.get();
	}
	public void setWins(int wins) {
		this.wins = new SimpleIntegerProperty(wins);
	}
	public int getLoses() {
		return loses.get();
	}
	public void setLoses(int loses) {
		this.loses = new SimpleIntegerProperty(loses);
	}
}
