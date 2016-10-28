package com.lte.controller;


import com.lte.aiPar.AlgorithmManager;
import com.lte.aiPar.SingleAlgorithm;
import com.lte.db.DBconnection;
import com.lte.gui.Controller1;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.GameInfo;
import com.lte.models.ServerMessage;
import com.lte.models.GameScore;

import javafx.application.Platform;

public class ThreadPlay extends Thread {

	// interface to server
	private InterfaceManager interfaceManager;
	long timeStart;

	// GUI controller
	private Controller1 controller1;

	// model(s)
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	AlgorithmManager algorithmManager;

	public ThreadPlay(InterfaceManager interfaceManager, Controller1 controller1, GameInfo gameInfo,
			DBconnection connection, AlgorithmManager algorithmManager) {
		this.interfaceManager = interfaceManager;
		this.controller1 = controller1;
		this.gameInfo = gameInfo;
		this.connection = connection;
		this.algorithmManager = algorithmManager;
		timeStart = 0;

	}

	@Override
	public void run() {

		System.out.println("Spielen läuft");

		// lade KI
		algorithmManager = new AlgorithmManager();
		System.out.println("KI geladen");
		// Starte neuen Thread um JavaFx zu befuellen
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller1.showReady();
			}
		});

		ServerMessage message = interfaceManager.receiveMessage();
		int opponentMoveStart = message.getOpponentMove();
		boolean startingRound = false;
		// set starting Player
		if (opponentMoveStart == -1) {
			// wir starten
			gameInfo.setNextPlayer('X');
		} else {
			gameInfo.setNextPlayer('O');
			startingRound = true;
		}

		// lade DB Controller
		int ids[] = connection.startNewGame(gameInfo.getOpponentName(), String.valueOf(gameInfo.getNextPlayer()));

		// prepare gameInfo
		gameInfo.setGameID(ids[0]);
		gameInfo.setSetID(ids[1]);
		gameInfo.setOpponentID(ids[2]);

		gameInfo.setGameInProgress(true);

		// lade Spielstand-Logik
		GameScore currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");
		
		

		while (true) {
			// ***** Gegner spielt Zug ******
			if (gameInfo.getNextPlayer() == 'O') {
				System.out.println("Gegner spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {
					if (startingRound) {
						startingRound = false;
					} else {
						message = interfaceManager.receiveMessage();
						timeStart = System.currentTimeMillis();
					}
					// - Spiel entschieden/gewonnen
					if (!message.getWinner().equals("offen")) {
						break;
					}
					int opponentMove = message.getOpponentMove();

					currentGameScore.play(opponentMove, 'O');

					// visualisiere in GUI
					int row = currentGameScore.getRow(opponentMove);

					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller1.fill(opponentMove, row, 'O', false);
						}
					});

					// Log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", opponentMove);

					gameInfo.setNextPlayer('X');
					//currentGameScore.print();
					continue;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ***** wir spielen Zug *****
			else if (gameInfo.getNextPlayer() == 'X') {
				System.out.println("Wir spielen");
				try {
					// berechne n�chsten Zug - KI gibt Spalte zur�ck
					int nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10, 'X', 'O');

					// sende n�chsten Zug an Server
					interfaceManager.sendMove(nextMove);
					
					final long timeEnd = System.currentTimeMillis(); 
			        System.out.println("Antwortzeit: " + (timeEnd - timeStart) + " Millisek.");

					currentGameScore.play(nextMove, 'X');

					// visualisiere in GUI
					int row = currentGameScore.getRow(nextMove);
					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller1.fill(nextMove, row, 'X', false);
						}
					});

					// log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", nextMove);

					gameInfo.setNextPlayer('O');
					//currentGameScore.print();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} // endwhile

		algorithmManager.shutdown();

		// ****** Spiel ist entschieden *******
		
		// TODO Zuordnung von X/O zu Teamnamen
		System.out.println("Message: " + message.getWinner() + " hat gewonnen");
		System.out.println("KI: Spieler " + currentGameScore.isWon() + " hat gewonnen");
		
		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1] ->
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller1.gameOver(currentGameScore.isWon(),currentGameScore.winWhere());
			}
		});

		
		//Ausgabe in Konsole zur Kontrolle
		System.out.println("Winning Kombi");
		int[][] woGewonnen = currentGameScore.winWhere();
		for (int i = 0; i < woGewonnen.length; i++) {
			System.out.print(woGewonnen[i][0] + " ");
			System.out.println(woGewonnen[i][1]);
		}
		
		
	}
}
