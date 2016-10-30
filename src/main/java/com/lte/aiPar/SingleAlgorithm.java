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
	public SingleAlgorithm(byte[][] field, int algorithmDepth){
	
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
       
		max((byte) startDepth, -1000000, 1000000);
		
		return savedMove;
	}
	
	
	/**
	 * Recursively processing of max step in alpha beta algorithm
	 * @param depth remaining algorithm depth
	 * @param alpha alpha value of current alpha beta
	 * @param beta beta value of current alpha beta
	 * @return
	 */
	private int max(byte depth, int alpha, int beta) {
	    if (depth == 0){
	       return bufferdGameScore.eval();
	    }
	    int maxValue = alpha;
	    byte[] possibleMoves = bufferdGameScore.possibleMoves();
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
	          if (depth == startDepth)
	             savedMove = possibleMoves[i];
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
	    if (depth == 0 || bufferdGameScore.possibleMoves()[0] == 99 || isWon != 'N'){
	    	return bufferdGameScore.eval();
	    }
	    int minValue = beta;
	    byte[] possibleMoves = bufferdGameScore.possibleMoves();
	    for (byte i = 0; i < possibleMoves.length; i++)
	    {
	    	if(possibleMoves[i] == 99){break;}
	    	try {
				bufferdGameScore.play(possibleMoves[i], (byte) 2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int value = max((byte) (depth-1), alpha, minValue);
	       try {
				bufferdGameScore.unDo(possibleMoves[i]);
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