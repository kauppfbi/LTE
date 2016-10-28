package com.lte.aiPar;


import java.util.ArrayList;

import com.lte.models.GameScore;

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
	
	public  int alphaBeta(){ 
       
		max(startDepth, -1000000, 1000000);
		

		return savedMove;
	}
	
	public  int max(int depth, int alpha, int beta) {
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
	
	
	 public  int min(int depth, int alpha, int beta) {
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
				bufferdGameScore.reDo(possibleMoves.get(i));
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