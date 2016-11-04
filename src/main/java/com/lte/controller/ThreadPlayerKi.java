package com.lte.controller;

import com.lte.aiPar.AlgorithmManager;
import com.lte.gui.Controller3;
import com.lte.models.GameInfo;
import com.lte.models.GameScore;
import com.lte.models.Settings;

import javafx.application.Platform;

public class ThreadPlayerKi {

	//**********************************//
	// GUI controller
	private Controller3 controller3;

	// model(s)
	private GameInfo gameInfo;
	private Settings settings;
	private GameScore currentGameScore;

	// KI Manager
	AlgorithmManager algorithmManager;
	
	private int nextMove;
//
////**********************************//
	public ThreadPlayerKi(Controller3 controller3, GameInfo gameInfo, AlgorithmManager algorithmManager, Settings settings) {
		this.controller3 = controller3;
		this.gameInfo = gameInfo;
		this.settings = settings;
		
		System.out.println("Spielen läuft");

		// lade Spielstand-Logik
		currentGameScore = new GameScore();
		currentGameScore.initialize();
		System.out.println("Spielstand initialisiert");
		
		this.algorithmManager = new AlgorithmManager();
		System.out.println("KI geladen");
	}

	
	
	public int playTurn(int column){
			// ***** Player spielt Zug ******
			if (gameInfo.getNextPlayer() == 'O') {
				System.out.println("Player spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {

						currentGameScore.play(column, (byte)2);

					// visualisiere in GUI -> Controller3 bei onClick

					gameInfo.setNextPlayer('X');

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ***** KI spielt Zug *****
			if (gameInfo.getNextPlayer() == 'X') {
				System.out.println("KI spielt");
				try {
					
					// berechne n�chsten Zug - KI gibt Spalte zur�ck
					nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getField(), 10, settings.getCalculationTime(), (byte) 1);

					currentGameScore.play(nextMove, (byte) 1);

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
		
		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1] ->
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller3.gameOver(currentGameScore.isWon(),currentGameScore.winWhere());
			}
		});
		
			}
			return nextMove;
	}
	
	
	
}
