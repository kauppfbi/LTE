package com.lte.controller;

import com.lte.gui.ControllerReconstruct;
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

	private ControllerReconstruct controller2;
	private int[] recTurns;
	private GameScore gameScore;

	/**
	 * constructor<br>
	 * 
	 * @param controller2
	 * @param recTurns
	 */
	public ThreadReconstruct(ControllerReconstruct controller2, int[] recTurns) {
		this.controller2 = controller2;
		this.recTurns = recTurns;
		this.gameScore = new GameScore();
		this.gameScore.initialize();
	}

	public void setRecTurns(int[] recTurns) {
		this.recTurns = recTurns;
	}

	/**
	 * Creates a Thread for each reconstructed Set<br>
	 * Reconstructs the Turns form recTurn-Array<br>
	 * calls the replayTurn-Method of Controller2 to visualize the Turn<br>
	 * allows User-interaction (stop,play,pause)<br>
	 */
	@Override
	public synchronized void run() {

		int[] rowIndex = new int[7];

		if (recTurns == null) {
			controller2.playRecFinished();
		} else {
			for (int i = 1; i < recTurns.length; i++) {

				int columnIndex = recTurns[i];

				int rowIndexTemp = 0;
				int color = 0;

				switch (recTurns[i]) {
				case 0:
					rowIndexTemp = rowIndex[0];
					rowIndex[0]++;
					break;
				case 1:
					rowIndexTemp = rowIndex[1];
					rowIndex[1]++;
					break;
				case 2:
					rowIndexTemp = rowIndex[2];
					rowIndex[2]++;
					break;
				case 3:
					rowIndexTemp = rowIndex[3];
					rowIndex[3]++;
					break;
				case 4:
					rowIndexTemp = rowIndex[4];
					rowIndex[4]++;
					break;
				case 5:
					rowIndexTemp = rowIndex[5];
					rowIndex[5]++;
					break;
				case 6:
					rowIndexTemp = rowIndex[6];
					rowIndex[6]++;
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
						e.printStackTrace();
					}

				} else if (recTurns[0] == 1) {
					recTurns[0] = 0;
					// green
					color = 1;
					try {
						gameScore.play(columnIndex, (byte) 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				final int fRowIndex = rowIndexTemp;
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
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller2.highlightWinning(gameScore.winWhere());
				}
			});
			controller2.playRecFinished();
		} // else
	}// run
}