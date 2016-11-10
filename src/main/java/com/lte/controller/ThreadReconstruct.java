package com.lte.controller;

import com.lte.gui.Controller2;
import com.lte.models.GameScore;

import javafx.application.Platform;

/**
 * ThreadReconstruct coordinates the Reconstruction of Games/Sets<br>
 * It creates a thread to coordinate the reconstruction Turns<br>
 * 
 * @author Florian
 *
 */
public class ThreadReconstruct extends Thread {

	private Controller2 controller2;
	private int[] recTurns;
	private GameScore gameScore;

	/**
	 * constructor<br>
	 * 
	 * @param controller2
	 * @param recTurns
	 */
	public ThreadReconstruct(Controller2 controller2, int[] recTurns) {
		this.controller2 = controller2;
		this.recTurns = recTurns;
		this.gameScore = new GameScore();
		this.gameScore.initialize();
	}

	public void setRecTurns(int[] recTurns) {
		this.recTurns = recTurns;
	}

	@Override
	/**
	 * Creates a Thread for each reconstructed Set<br>
	 * Reconstructs the Turns form recTurn-Array<br>
	 * calls the replayTurn-Method of Controller2 to visualize the Turn<br>
	 * allows User-interaction (stop,play,pause)<br>
	 * 
	 */
	public synchronized void run() {

		int rowIndex0 = 0;
		int rowIndex1 = 0;
		int rowIndex2 = 0;
		int rowIndex3 = 0;
		int rowIndex4 = 0;
		int rowIndex5 = 0;
		int rowIndex6 = 0;
		
		if(recTurns == null){
			controller2.playRecFinished();
		} else {
			for (int i = 1; i < recTurns.length; i++) {
	
				int columnIndex = recTurns[i];
				
				int rowIndex = 0;
				int color = 0;
	
				switch (recTurns[i]) {
				case 0:
					rowIndex = rowIndex0;
					rowIndex0++;
					break;
				case 1:
					rowIndex = rowIndex1;
					rowIndex1++;
					break;
				case 2:
					rowIndex = rowIndex2;
					rowIndex2++;
					break;
				case 3:
					rowIndex = rowIndex3;
					rowIndex3++;
					break;
				case 4:
					rowIndex = rowIndex4;
					rowIndex4++;
					break;
				case 5:
					rowIndex = rowIndex5;
					rowIndex5++;
					break;
				case 6:
					rowIndex = rowIndex6;
					rowIndex6++;
					break;
				default:
					break;
				}
	
				// logic for color select
				if (recTurns[0] == 0) {
					recTurns[0] = 1;
					// blue
					color = 0;
					try {
						gameScore.play(columnIndex, (byte) 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else if (recTurns[0] == 1) {
					recTurns[0] = 0;
					// green
					color = 1;
					try {
						gameScore.play(columnIndex, (byte) 2);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
				final int fRowIndex = rowIndex;
				final int fColor = color;
	
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						controller2.replayTurn(columnIndex, fRowIndex, fColor);
					}
				});
	
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					try {
						this.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			// for
			// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[4][1] ->
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller2.highlightWinning(gameScore.winWhere());
				}
			});
			controller2.playRecFinished();
		}//else
	}// run
}