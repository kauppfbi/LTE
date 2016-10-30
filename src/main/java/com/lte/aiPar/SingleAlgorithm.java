package com.lte.aiPar;


import java.util.ArrayList;

import com.lte.models.GameScore;

/**
 * The class SingleAlgorithm implements a single threaded version of an alpha beta algorithm and can be used by the AlgortihmManager.
 * @author Fabian Sölker
 *
 */
public class SingleAlgorithm{
	
	//*********Klassenvariablen****************************
	private int startDepth;
	private int savedMove;
	private GameScore bufferdGameScore;
	
	//********Konstruktoren********************************
	public SingleAlgorithm(char[][] field, int algorithmDepth){
	
	//Ereugen neuen Spielstand f�r den Algorithmus
	this.bufferdGameScore = new GameScore(field);
	
	//Gew�nschte Tiefe
	this.startDepth = algorithmDepth;
	}
	
	//*******Methoden**************************************
	
	/**
	 * Performs single threaded alpha beta algorithm 
	 * @return savedMove as integer
	 */
	public int alphaBeta(){ 
       
		max(startDepth, -1000000, 1000000);
		
		return savedMove;
	}
	
	
	/**
	 * Recursively processing of max step in alpha beta algorithm
	 * @param depth remaining algorithm depth
	 * @param alpha alpha value of current alpha beta
	 * @param beta beta value of current alpha beta
	 * @return
	 */
	private int max(int depth, int alpha, int beta) {
	    if (depth == 0){
	       return bufferdGameScore.eval();
	    }
	    int maxValue = alpha;
	    ArrayList<Integer> possibleMoves = bufferdGameScore.possibleMoves();
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
	    if (depth == 0 || bufferdGameScore.possibleMoves().size() == 0){
	    	return bufferdGameScore.eval();
	    }
	    int minValue = beta;
	    ArrayList<Integer> possibleMoves = bufferdGameScore.possibleMoves();
	    for (int i = 0; i < possibleMoves.size(); i++)
	    {
	    	try {
				bufferdGameScore.play(possibleMoves.get(i), 'O');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int value = max(depth-1, alpha, minValue);
	       try {
				bufferdGameScore.unDo(possibleMoves.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       if (value < minValue) {
	          minValue = value;
	          if (minValue <= alpha)
	             break;
	       }
	    }
	    return minValue;
	 }
	
	

}