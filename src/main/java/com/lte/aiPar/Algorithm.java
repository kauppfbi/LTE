package com.lte.aiPar;


import java.util.concurrent.Callable;

import com.lte.models.GameScore;

/**
 * The class Algorithm implements callable for multi threaded usage in AlgortihmManager. Call() returns the min value for the corresponding subtree of alpha beta. 
 * @author Fabian Soelker
 *
 */
public class Algorithm implements Callable<Integer>{
	
	//*********Klassenvariablen****************************
	private int startDepth;
	private GameScore bufferdGameScore;
	
	//********Konstruktoren********************************
	public Algorithm(byte[][] field, int algorithmDepth){
	
	//Ereuge neuen Spielstand f�r den Algorithmus
	bufferdGameScore = new GameScore(field);
	
	//Gew�nschte Tiefe
	startDepth = algorithmDepth;

	
	}
	
	//*******Methoden**************************************
	
	/**
	 * Recursively processing of max step in alpha beta algorithm
	 * @param depth remaining algorithm depth
	 * @param alpha alpha value of current alpha beta
	 * @param beta beta value of current alpha beta
	 * @return
	 */
	private int max(byte depth, int alpha, int beta) {
		char isWon = bufferdGameScore.isWon();
	    byte[] possibleMoves = bufferdGameScore.possibleMoves();
	    if (depth == 0 || possibleMoves[0] == 99 || isWon != 'N'){
	    	if(isWon != 'N'){
	    		return (bufferdGameScore.eval() * ((depth + 1)) * 100);
	    	}else{
		    	return bufferdGameScore.eval();
	    	}
	    }
	    int maxValue = alpha;
	    for (byte i = 0; i < possibleMoves.length; i++)
	    {
	    	if(possibleMoves[i] == 99){break;}
	       try {
			bufferdGameScore.play(possibleMoves[i], (byte) 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       int value = min((byte) (depth-1), maxValue, beta);
	       try {
			bufferdGameScore.unDo(possibleMoves[i]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       if (value > maxValue) {
	          maxValue = value;
	          if (maxValue >= beta)
	             break;
	       }
	    }
	    return maxValue;
	 }
	
	
	/**
	 * Recursively processing of min step in alpha beta algorithm
	 * @param depth remaining algorithm depth
	 * @param alpha alpha value of current alpha beta
	 * @param beta beta value of current alpha beta
	 * @return
	 */
	 private int min(byte depth, int alpha, int beta) {
		char isWon = bufferdGameScore.isWon();
	    byte[] possibleMoves = bufferdGameScore.possibleMoves();
	    if (depth == 0 || possibleMoves[0] == 99 || isWon != 'N'){
	    	if(isWon != 'N'){
	    		return (bufferdGameScore.eval() * ((depth + 1)) * 100);
	    	}else{
		    	return bufferdGameScore.eval();
	    	}
	    }
	    int minValue = beta;
	    for (byte i = 0; i < possibleMoves.length; i++)
	    {
	    	if(possibleMoves[i] == 99){break;}
	    	try {
				bufferdGameScore.play(possibleMoves[i], (byte) 2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int wert = max((byte) (depth-1), alpha, minValue);
	       try {
				bufferdGameScore.unDo(possibleMoves[i]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       if (wert < minValue) {
	          minValue = wert;
	          if (minValue <= alpha)
	             break;
	       }
	    }
	    return minValue;
	 }


	@Override
	public Integer call() throws Exception {

			int min = min((byte) startDepth, -1000000, 1000000);					
			return min;
	}
	
	

}
