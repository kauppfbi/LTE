package com.lte.controller;

import com.lte.gui.Controller2;

import javafx.application.Platform;



/**
 * ThreadReconstruct coordinates the Reconstruction of Games/Sets<br>
 * Main Activities are:<br>
 * - loading played Games into GameID<br>
 * - loading played Sets into SetID<br>
 * => coordinate communication between GUI and DB!
 * 
 * @author Florian
 *
 */
public class ThreadReconstruct extends Thread{
	
	private Controller2 controller2;
	private int[] recTurns;
	
	/**
	 * 
	 * @param controller2
	 * @param recTurns
	 */
	public ThreadReconstruct(Controller2 controller2, int[] recTurns){
		this.controller2 = controller2;
		this.recTurns = recTurns;
	}
	
	public int[] getRecTurns() {
		return recTurns;
	}

	public void setRecTurns(int[] recTurns) {
		this.recTurns = recTurns;
	}

	
	// TODO @Fabian Soelker: Der Thread run() blockiert uns waehrend der Ausfuehrung das gesamte Programm, kannst du das Fixen??
	@Override
	/**
	 * fillRec method replays the turns of the selected set into the GridPane gameGrid<br>
	 * fillRec is called by playRec-method in Controller2<br>
	 * playRec-method listens to Button "Play"<br>
	 */
	public void run(){
		
		int rowIndex0 = 0;
		int rowIndex1 = 0;
		int rowIndex2 = 0;
		int rowIndex3 = 0;
		int rowIndex4 = 0;
		int rowIndex5 = 0;			
		int rowIndex6 = 0;
			
		for(int i = 1; i < recTurns.length; i++){
			
			int columnIndex = recTurns[i];
			int rowIndex = 0;
			int color = 0;
				
			switch(recTurns[i]){
				case 0: rowIndex = rowIndex0;
						rowIndex0++;
						break;
				case 1: rowIndex = rowIndex1;
						rowIndex1++;
						break;
				case 2: rowIndex = rowIndex2;
						rowIndex2++;
						break;
				case 3: rowIndex = rowIndex3;
						rowIndex3++;
						break;
				case 4: rowIndex = rowIndex4;
						rowIndex4++;
						break;
				case 5: rowIndex = rowIndex5;
						rowIndex5++;
						break;
				case 6: rowIndex = rowIndex6;
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
			} else if (recTurns[0] == 1) {
				recTurns[0] = 0;
				// green
				color = 1;
			}
			
			final int frowIndex = rowIndex;
			final int fcolor = color;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller2.replayTurn(columnIndex, frowIndex, fcolor);
				}
			});
			
			try {
				// problem: Thread does not lose the ownership of the monitor...
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			//end of run()
			
	}//run
}//class

	
}