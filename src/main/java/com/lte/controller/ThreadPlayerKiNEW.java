package com.lte.controller;

import com.lte.aiPar.AlgorithmManager;
import com.lte.db.DBconnection;
import com.lte.gui.Controller3;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;
import com.lte.models.Settings;

import javafx.application.Platform;

public class ThreadPlayerKiNEW extends Thread {
	
	private Controller3 controller3;
	private AlgorithmManager algorithmManager;
	private GameInfo gameInfo;
	private Settings settings;
	private GameScore currentGameScore;
	private DBconnection connection;
	private int nextMove;
	
	public ThreadPlayerKiNEW(Controller3 controller3, GameInfo gameInfo, Settings settings, AlgorithmManager algorithmManager){
		this.controller3 = controller3;
		this.gameInfo = gameInfo;
		this.settings = settings;
		this.algorithmManager = algorithmManager;
		
		currentGameScore = new GameScore();
		currentGameScore.initialize();

		// load DB Controller
		if (gameInfo.getSetID() == -1) {
			int ids[] = connection.startNewGame(gameInfo.getOpponentName(), String.valueOf(gameInfo.getNextPlayer()));
			// prepare gameInfo
			gameInfo.setGameID(ids[0]);
			gameInfo.setSetID(ids[1]);
			gameInfo.setOpponentID(ids[2]);
		} else {
			gameInfo.setSetID(connection.createNewSet(gameInfo.getGameID(), gameInfo.getOwnPoints(),
					gameInfo.getOpponentPoints(), String.valueOf(gameInfo.getNextPlayer())));
		}
	}
	
	public void playTurn(int column) {
		
		// User plays next turn
		if (gameInfo.getNextPlayer() == 'O') {
			System.out.println("Player spielt!");
			
			try {
				currentGameScore.play(column, (byte) 2);
				connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", column);
				gameInfo.setNextPlayer('X');
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// KI plays next turn
		if (gameInfo.getNextPlayer() == 'X' && currentGameScore.isWon() == 0) {
			System.out.println("KI spielt");
			try {
				// get the column of the next Turn
				nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10,
						settings.getCalculationTime(), (byte) 1);
				currentGameScore.play(nextMove, (byte) 1);
				connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", nextMove);
				gameInfo.setNextPlayer('O');
				currentGameScore.print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// check if current Game is Won
		if (currentGameScore.isWon() != 0) {

			algorithmManager.shutdown();

			// TODO Zuordnung von X/O zu Teamnamen
			System.out.println("KI: " + currentGameScore.isWon() + " hat gewonnen");

			// - Gewinner in DB schreiben + Punkte hochzaehhlen
			if (currentGameScore.isWon() == 2) {
				connection.updateWinnerOfSet(gameInfo.getSetID(), "O");
				gameInfo.setOpponentPoints(gameInfo.getOpponentPoints() + 1);
			} else if (currentGameScore.isWon() == 1) {
				connection.updateWinnerOfSet(gameInfo.getSetID(), "X");
				gameInfo.setOwnPoints(gameInfo.getOwnPoints() + 1);
			} else {
				connection.updateWinnerOfSet(gameInfo.getSetID(), "U");
			}

			// Prüfen ob Game zu Ende und in DB schreiben
			if (gameInfo.getOwnPoints() == 3) {
				connection.updateScoreOfGame(gameInfo.getGameID(), gameInfo.getOwnPoints(),
						gameInfo.getOpponentPoints(), "X");
			}
			if (gameInfo.getOpponentPoints() == 3) {
				connection.updateScoreOfGame(gameInfo.getGameID(), gameInfo.getOwnPoints(),
						gameInfo.getOpponentPoints(), "O");
			}

			// Spieler für Beginn der nächsten Runde bestimmen
			if (gameInfo.getStartingPlayer() == 'X') {
				gameInfo.setNextPlayer('O');
				gameInfo.setStartingPlayer('O');
			} else if (gameInfo.getStartingPlayer() == 'O') {
				gameInfo.setNextPlayer('X');
				gameInfo.setStartingPlayer('X');
			}
		}
	}
	
	public synchronized void run(){
		int columnIndex = 0;
		int rowIndex = 0;
		char player = 'X';
		boolean endGame = false;
		
		Platform.runLater(new Runnable() {
			public void run(){
				controller3.fill(columnIndex, rowIndex, player, endGame);
			}
		});
	}
	
	
	
}