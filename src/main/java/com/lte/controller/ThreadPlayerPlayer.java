package com.lte.controller;

import com.lte.gui.Controller4;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;

import javafx.application.Platform;

/**
 * The class implements a game of player against player
 * @author Fabian Soelker
 *
 */
public class ThreadPlayerPlayer {

	//**********************************//
	// GUI controller
	private Controller4 controller4;

	// model(s)
	private GameInfo gameInfo;
	private GameScore currentGameScore;

	
	private int nextMove;
//
////**********************************//
	public ThreadPlayerPlayer(Controller4 controller4, GameInfo gameInfo) {
		this.controller4 = controller4;
		this.gameInfo = gameInfo;
		
		System.out.println("Spielen läuft");

		// lade Spielstand-Logik
		currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");
		
	}

	
	/**
	 * Plays the next move in the logic, sets the next player and checks if the game is over
	 * @param column of the corresponding event
	 * @return column if game is not interrupted by game over
	 */
	public int playTurn(int column){
			// ***** Player O spielt Zug ******
			if (gameInfo.getNextPlayer() == 'O') {
				System.out.println("Player O spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {

						currentGameScore.play(column, (byte)2);

					// visualisiere in GUI -> Controller3 bei onClick

					gameInfo.setNextPlayer('X');

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ***** Player X spielt Zug ******
			else if (gameInfo.getNextPlayer() == 'X') {
				System.out.println("Player X spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {

						currentGameScore.play(column, (byte)1);

					// visualisiere in GUI -> Controller3 bei onClick

					gameInfo.setNextPlayer('O');

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ****** Spiel ist entschieden *******
			if (currentGameScore.isWon() != 0) {
				
		
		// TODO Zuordnung von X/O zu Teamnamen
		System.out.println("Logik: " + currentGameScore.isWon() + " hat gewonnen");
		
		//Spieler für Beginn der nächsten Runde bestimmen
		if(gameInfo.getStartingPlayer() == 'X'){
			gameInfo.setNextPlayer('O');
			gameInfo.setStartingPlayer('O');
		}
		else if(gameInfo.getStartingPlayer() == 'O'){
			gameInfo.setNextPlayer('X');
			gameInfo.setStartingPlayer('X');
		}
		
		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1] ->
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller4.gameOver(currentGameScore.isWon(),currentGameScore.winWhere());
			}
		});
		
			}
			return nextMove;
	}
	
	
	
}

