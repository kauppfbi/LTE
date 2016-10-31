package com.lte.controller;

import com.lte.aiPar.AlgorithmManager;
import com.lte.gui.Controller1;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;
import com.lte.models.Settings;

import javafx.application.Platform;

public class ThreadPlayerKi {

	//**********************************//
	// GUI controller
//	private Controller3 controller3;

	// model(s)
	private GameInfo gameInfo;
	private Settings settings;

	// KI Manager
	AlgorithmManager algorithmManager;
//	private int playerMove;
//
////**********************************//
//	public ThreadPlayKi(Controller3 controller3, GameInfo gameInfo, AlgorithmManager algorithmManager, Settings settings) {
//		this.controller3 = controller3;
//		this.gameInfo = gameInfo;
//		this.algorithmManager = algorithmManager;
//		this.settings = settings;

	
	public void play() {

		System.out.println("Spielen läuft");

		
			//Spieler X beginnt. Hier Zufallswahl einbauen !!!
			boolean startingRound = true;


		// lade Spielstand-Logik
		GameScore currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");
		
		

		while (true) {
			// ***** Player spielt Zug ******
			if (gameInfo.getNextPlayer() == 'O') {
				System.out.println("Player spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {
					if (startingRound) {
						startingRound = false;
					} else {

						//!!! Hier Player input!!!
//						playerMove = 0;
//						currentGameScore.play(playerMove, (byte)2);
						
					}
					// - Spiel entschieden/gewonnen
					if (currentGameScore.isWon() != 'N') {
						break;
					}


					// visualisiere in GUI
//					int row = currentGameScore.getRow(playerMove);

					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
//							controller3.fill(playerMove, row, 'O', false);
						}
					});


					gameInfo.setNextPlayer('X');
					continue;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ***** KI spielen Zug *****
			else if (gameInfo.getNextPlayer() == 'X') {
				System.out.println("KI spielt");
				try {
					// berechne n�chsten Zug - KI gibt Spalte zur�ck
					int nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10, settings.getCalculationTime(), (byte) 1);

					currentGameScore.play(nextMove, (byte) 1);

					// visualisiere in GUI
					int row = currentGameScore.getRow(nextMove);
					// Starte neuen Thread um JavaFx zu befuellen
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
//							controller3.fill(nextMove, row, 'X', false);
						}
					});

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
		System.out.println("KI: " + currentGameScore.isWon() + " hat gewonnen");
		
		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1] ->
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
//				controller3.gameOver(currentGameScore.isWon(),currentGameScore.winWhere());
			}
		});
		
		
	}
	
	
	
}
