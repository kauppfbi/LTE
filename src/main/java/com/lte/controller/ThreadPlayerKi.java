package com.lte.controller;

import java.io.IOException;

import com.lte.aiPar.AlgorithmManager;
import com.lte.gui.Controller3;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;
import com.lte.models.Settings;

import javafx.application.Platform;

public class ThreadPlayerKi {

	// hallo
	// **********************************//
	// GUI controller
	private Controller3 controller3;

	// model(s)
	private GameInfo gameInfo;
	private Settings settings;
	private GameScore currentGameScore;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	AlgorithmManager algorithmManager;

	private int nextMove;

	//// **********************************//
	public ThreadPlayerKi(Controller3 controller3, GameInfo gameInfo, AlgorithmManager algorithmManager,
			Settings settings, DBconnection connectionInput) {
		this.controller3 = controller3;
		this.gameInfo = gameInfo;
		this.settings = settings;
		this.connection = connectionInput;

		System.out.println("Spielen läuft");

		// lade Spielstand-Logik
		currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");

		this.algorithmManager = algorithmManager;
		System.out.println("KI geladen");

		// lade DB Controller
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

	public int playTurn(int column) {
		// ***** Player spielt Zug ******
		if (gameInfo.getNextPlayer() == 'O') {
			System.out.println("Player spielt!");
			// -Spiele Gegnerzug im Spielstand
			try {
				currentGameScore.play(column, (byte) 2);
				// Log turn in DB
				connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", column);

				gameInfo.setNextPlayer('X');

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ***** KI spielt Zug *****
		if (gameInfo.getNextPlayer() == 'X' && currentGameScore.isWon() == 0) {
			System.out.println("KI spielt");
			try {

				// berechne n�chsten Zug - KI gibt Spalte zur�ck
				nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10,
						settings.getCalculationTime(), (byte) 1);

				currentGameScore.play(nextMove, (byte) 1);

				// log turn in DB
				connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", nextMove);

				gameInfo.setNextPlayer('O');
				currentGameScore.print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ****** Spiel ist entschieden *******
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

			// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1]
			// ->
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller3.gameOver(currentGameScore.isWon(), currentGameScore.winWhere());
				}
			});
		}

		return nextMove;
	}
}
