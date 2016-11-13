package com.lte.controller;

import com.lte.aiPar.AlgorithmManager;
import com.lte.gui.ControllerPlayerKi;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;
import com.lte.models.Settings;

import javafx.application.Platform;

/**
 * The class implements a game of a player against the AI
 * @author Fabian Soelker
 *
 */
public class ThreadPlayerKiNEW extends Thread {
	
	private ControllerPlayerKi controller3;
	private AlgorithmManager algorithmManager;
	private GameInfo gameInfo;
	private Settings settings;
	private GameScore currentGameScore;
	private DBconnection connection;
	private int nextMove;
	private boolean ready;
	
	public ThreadPlayerKiNEW(ControllerPlayerKi controller3, GameInfo gameInfo, Settings settings, AlgorithmManager algorithmManager, DBconnection connection){
		this.controller3 = controller3;
		this.gameInfo = gameInfo;
		this.settings = settings;
		this.algorithmManager = algorithmManager;
		this.connection = connection;
		this.currentGameScore = new GameScore();
		this.ready = false;
	}
	
	/**
	 * The Method checks if the given move is possible on the current field
	 * @param nextMove next move given by the listener
	 * @throws Exception If move is not possible on the current field
	 */
	public void setNextMove(int nextMove) throws Exception{
		
		if(nextMove != -1){
			currentGameScore.play(nextMove, (byte) 3);
			currentGameScore.unDo(nextMove);
			this.nextMove = nextMove;
		}else{
			this.nextMove = nextMove;
		}
	}
	
	/**
	 * Runs a a whole game in a thread
	 */
	public synchronized void run(){
		int move;
		int row;
		currentGameScore.initialize();
		
		if (gameInfo.getSetID() == -1) {
			// prepare gameInfo, if there is no current Game
			int ids[] = connection.startNewGame(gameInfo.getOpponentName(), String.valueOf(gameInfo.getNextPlayer()));
			gameInfo.setGameID(ids[0]);
			gameInfo.setSetID(ids[1]);
			gameInfo.setOpponentID(ids[2]);
		} else {
			// new set of current game
			gameInfo.setSetID(connection.createNewSet(gameInfo.getGameID(), gameInfo.getOwnPoints(),
					gameInfo.getOpponentPoints(), String.valueOf(gameInfo.getNextPlayer())));
		}
		
		ready = true;
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(true){
			System.out.println("in While, nextMove: " + nextMove);
			if(nextMove == -1){
				System.out.println("Sonderfall: KI fängt an!");
				// TODO: algorithmDepth ordentlich!!
				try {
					move = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10, settings.getCalculationTime(), (byte)1);
					currentGameScore.play(move, (byte) 1);
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", move);
					row = currentGameScore.getRow(move);
					final int frow3 = row;
					final int fmove = move;
					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							controller3.fill(fmove, frow3, 'X');
						}
					});
					gameInfo.setNextPlayer('O');
					this.wait();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}	
			} else{
				try {
					currentGameScore.play(nextMove, (byte) 2);
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", nextMove);
					byte winner = currentGameScore.isWon();
					
					if(winner == 0){
						//no winner so far
						row = currentGameScore.getRow(nextMove);
						final int frow = row;
						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								controller3.fill(nextMove, frow, 'O');
							}
						}); 
						move = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10, settings.getCalculationTime(), (byte)1);
						currentGameScore.play(move, (byte) 1);
						connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", move);
						row = currentGameScore.getRow(move);
						
						final int frow2 = row;
						final int fmove2 = move;
						
						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								controller3.fill(fmove2, frow2, 'X');
							}
						}); 
						winner = currentGameScore.isWon();
						
						if(winner == 0){
							gameInfo.setNextPlayer('O');
							this.wait();
							continue;
						} else if(winner == 1){
							System.out.println("GameOver, KI gewinnt");
							break;
						}
					} else if (winner == 2){
						System.out.println("GameOver, User gewinnt");
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		algorithmManager.shutdown();
		
		// - Gewinner in DB schreiben + Punkte hochzaehhlen
		if(currentGameScore.isWon() == 2){
			connection.updateWinnerOfSet(gameInfo.getSetID(), "O");
			gameInfo.setOpponentPoints(gameInfo.getOpponentPoints() + 1);
		}
		else if(currentGameScore.isWon() == 1){
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
		
		Platform.runLater(new Runnable (){

			@Override
			public void run() {
				controller3.gameOver(currentGameScore.isWon(), currentGameScore.winWhere());
			}
			
		});
	}

	/**
	 * Checks if the thread is ready for the next event
	 * @return returns boolean false or true
	 */
	public boolean isReady() {
		return ready;
	}
}
