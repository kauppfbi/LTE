package com.lte.controller;

import javax.swing.JOptionPane;

import com.lte.aiPar.AlgorithmusManager;
import com.lte.db.DBconnection;
import com.lte.gui.Controller0;
import com.lte.gui.Controller1;
import com.lte.gui.Controller2;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.GameInfo;
import com.lte.models.ServerMessage;
import com.lte.models.Settings;
import com.lte.models.Spielstand;

import javafx.application.Platform;

public class ThreadPlay extends Thread {

	// interface to server
	private InterfaceManager interfaceManager;

	// GUI controller
	private Controller1 controller1;

	// model(s)
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	AlgorithmusManager algorithmManager;

	public ThreadPlay(InterfaceManager interfaceManager, Controller1 controller1, GameInfo gameInfo,
			DBconnection connection, AlgorithmusManager algorithmManager) {
		this.interfaceManager = interfaceManager;
		this.controller1 = controller1;
		this.gameInfo = gameInfo;
		this.connection = connection;
		this.algorithmManager = algorithmManager;
	}

	@Override
	public void run() {

		System.out.println("Spielen läuft");

		// lade KI
		algorithmManager = new AlgorithmusManager();
		System.out.println("KI geladen");

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
		Spielstand currentGameScore = new Spielstand();
		currentGameScore.initialisiere();
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
					}
					// - Spiel entschieden/gewonnen
					if (!message.getWinner().equals("offen")) {
						break;
					}
					int opponentMove = message.getOpponentMove();

					currentGameScore.spiele(opponentMove, 'O');

					// visualisiere in GUI
					int row = currentGameScore.getZeile(opponentMove);

					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller1.fill(opponentMove, row, gameInfo.getNextPlayer(), false);
						}
					});

					// Log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", opponentMove);

					gameInfo.setNextPlayer('X');
					currentGameScore.print();
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
					int nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getSpielfeld(), 10, 'X', 'O');

					// sende n�chsten Zug an Server
					interfaceManager.sendMove(nextMove);

					currentGameScore.spiele(nextMove, 'X');

					// visualisiere in GUI
					int row = currentGameScore.getZeile(nextMove);
					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller1.fill(nextMove, row, gameInfo.getNextPlayer(), false);
						}
					});

					// log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", nextMove);

					gameInfo.setNextPlayer('O');
					currentGameScore.print();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} // endwhile

		algorithmManager.shutdown();

		// ****** Spiel ist entschieden *******

		// TODO Zuordnung von X/O zu Teamnamen
		System.out.println("Spieler: " + message.getWinner() + " hat gewonnen");

		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[3][1] ->
		// [Pos][Spalte] / [Pos][Zeile]
		// System.out.println(aktuellerSpielstand.woGewonnen());

		// controller1.gameOver();
		// TODO gameOver(String winnerTeam, int[] winnerCombo);
	}
}
