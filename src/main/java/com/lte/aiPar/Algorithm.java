package com.lte.aiPar;


import java.util.ArrayList;
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
	private int savedMove;
	private GameScore bufferdGameScore;
	
	//********Konstruktoren********************************
	public Algorithm(char[][] field, int algorithmDepth){
	
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
	private int max(int depth, int alpha, int beta) {
		char isWon = bufferdGameScore.isWon();
	    ArrayList<Integer> possibleMoves = bufferdGameScore.possibleMoves();
	    if (depth == 0 || possibleMoves.size() == 0 || isWon != 'N'){
	    	if(isWon != 'N'){
	    		return (bufferdGameScore.eval() * ((depth + 1)) * 100);
	    	}else{
		    	return bufferdGameScore.eval();
	    	}
	    }
	    int maxValue = alpha;
	    for (int i = 0; i < possibleMoves.size(); i++)
	    {
	       try {
			bufferdGameScore.play(possibleMoves.get(i), 'X');
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       int value = min(depth-1, maxValue, beta);
	       try {
			bufferdGameScore.unDo(possibleMoves.get(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       if (value > maxValue) {
	          maxValue = value;
	          if (maxValue >= beta)
	             break;
	          if (depth == startDepth)
	             savedMove = possibleMoves.get(i);
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
	 private int min(int depth, int alpha, int beta) {
		char isWon = bufferdGameScore.isWon();
	    ArrayList<Integer> possibleMoves = bufferdGameScore.possibleMoves();
	    if (depth == 0 || possibleMoves.size() == 0 || isWon != 'N'){
	    	if(isWon != 'N'){
	    		return (bufferdGameScore.eval() * ((depth + 1)) * 100);
	    	}else{
		    	return bufferdGameScore.eval();
	    	}
	    }
	    int minValue = beta;
	    for (int i = 0; i < possibleMoves.size(); i++)
	    {
	    	try {
				bufferdGameScore.play(possibleMoves.get(i), 'O');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int wert = max(depth-1, alpha, minValue);
	       try {
				bufferdGameScore.unDo(possibleMoves.get(i));
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

			int min = min(startDepth, -1000000, 1000000);					
			return min;
	}
	
	

}
