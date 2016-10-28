package com.lte.aiPar;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.lte.models.GameScore;

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
	
	
	public int max(int depth, int alpha, int beta) {
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
			bufferdGameScore.reDo(possibleMoves.get(i));
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
	
	
	 public int min(int depth, int alpha, int beta) {
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
				bufferdGameScore.reDo(possibleMoves.get(i));
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
