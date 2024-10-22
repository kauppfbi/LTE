package com.lte.controller;

import com.lte.gui.ControllerPlayerPlayer;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;

import javafx.application.Platform;

/**
 * The class implements a game of player against player
 * 
 * @author Fabian Soelker
 *
 */
public class ThreadPlayerPlayer {

	// **********************************//
	// GUI controller
	private ControllerPlayerPlayer controllerPlayerPlayer;

	// model(s)
	private GameInfo gameInfo;
	private GameScore currentGameScore;

	/**
	 * Default Constructor
	 * 
	 * @param controllerPlayerPlayer
	 * @param gameInfo
	 */
	public ThreadPlayerPlayer(ControllerPlayerPlayer controllerPlayerPlayer, GameInfo gameInfo) {
		this.controllerPlayerPlayer = controllerPlayerPlayer;
		this.gameInfo = gameInfo;

		System.out.println("Spielen läuft");

		// lade Spielstand-Logik
		currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");

	}

	/**
	 * Plays the next move in the logic, sets the next player and checks if the
	 * game is over
	 * 
	 * @param column
	 *            of the corresponding event
	 * @throws Exception
	 *             if move is not possible on the current field
	 */
	public void playTurn(int column) throws Exception {
		// ***** Player O spielt Zug ******
		if (gameInfo.getNextPlayer() == 'O') {
			//System.out.println("Player O spielt!");
			// -Spiele Gegnerzug im Spielstand

			currentGameScore.play(column, (byte) 2);

			// visualisiere in GUI -> Controller3 bei onClick

			gameInfo.setNextPlayer('X');

		}

		// ***** Player X spielt Zug ******
		else if (gameInfo.getNextPlayer() == 'X') {
			//System.out.println("Player X spielt!");
			// -Spiele Gegnerzug im Spielstand

			currentGameScore.play(column, (byte) 1);

			// visualisiere in GUI -> Controller3 bei onClick

			gameInfo.setNextPlayer('O');

		}

		// ****** Spiel ist entschieden *******
		if (currentGameScore.isWon() != 0) {

			System.out.println("Logik: " + currentGameScore.isWon() + " hat gewonnen");

			// Spieler für Beginn der nächsten Runde bestimmen
			if (gameInfo.getStartingPlayer() == 'X') {
				gameInfo.setNextPlayer('O');
				gameInfo.setStartingPlayer('O');
			} else if (gameInfo.getStartingPlayer() == 'O') {
				gameInfo.setNextPlayer('X');
				gameInfo.setStartingPlayer('X');
			}

			// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1]
			// ->
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controllerPlayerPlayer.gameOver(currentGameScore.isWon(), currentGameScore.winWhere());
				}
			});

		}
	}

}
