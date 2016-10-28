package com.lte.aiPar;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lte.models.GameScore;

public class AlgorithmManager {

	//************Klassenvariablen*******************************************
		final ExecutorService service;
		List<Callable<Integer>> list = new ArrayList<> ();
		List<Future<Integer>> future = new ArrayList<> ();
		int roundCounter = 0;
		
		//************Konstruktoren************************************************
		public AlgorithmManager() {
			service = Executors.newFixedThreadPool(7); 
				
		}	
			
		
		
		//*************Methoden*****************************************************

		//******Shutdown********************************
		/**
		 * Shuts thread pool down
		 */
		public void shutdown(){
			service.shutdownNow();
		}
		
		//******Alpha Beta**************************************
		/**
		 * Multi threaded implementation of alpha beta algorithm to compute the next move
		 * @param field current field for processing the next move
		 * @param algorithmDepth algorithm depth for alpha beta algorithm
		 * @param playerCharacter character of own player
		 * @param opponent character of opponent
		 * @return best move as integer
		 * @throws Exception
		 */
		public int ParallelAlphaBeta(char[][] field, int algorithmDepth, char playerCharacter, char opponent) throws Exception{
			
			
			//Laufzeitanalyse starten
			final long timeStart = System.currentTimeMillis(); 
			
			//Arrays l�schen
			list.clear();
			future.clear();
			
			//H�chstwert und Spalte
			int highest = -1000000;
			
			//Neuen Spielstand aufbauen
			GameScore parAlgGameScore = new GameScore(field);
			
			//M�gliche Z�ge
			ArrayList<Integer> moves = new ArrayList<>();
			moves = parAlgGameScore.possibleMoves();
			
			//Die ersten Runden beschleunigen
			roundCounter++;

			if(roundCounter <= 7){
				algorithmDepth = 8;
			}
			
			System.out.println(algorithmDepth);
			
			//Pr�fe ob nur noch ein Zug vorhanden ist
			if(moves.size() == 1)
			{
				return moves.get(0);
			}
			
			//Pr�fe ob in den n�chsten Zuegen gewonnen werden kann
			SingleAlgorithm simpleAlg = new SingleAlgorithm(field, 4);
			int simpleMove = simpleAlg.alphaBeta();
			parAlgGameScore.play(simpleMove, playerCharacter);
			int eval = parAlgGameScore.eval();
			if (eval > 95000) {
				
				final long timeEnd1 = System.currentTimeMillis(); 
		        System.out.println("Laufzeit 4er: " + (timeEnd1 - timeStart) + " Millisek.");
		        return simpleMove;
		        
			}
			else {
				parAlgGameScore.unDo(simpleMove);
			}
			
			
			//Spiele die ersten Z�ge und erzeuge neue Tasks
			for (int i = 0; i < moves.size(); i++) {
				parAlgGameScore.play(moves.get(i), playerCharacter);
				
				list.add(new Algorithm(parAlgGameScore.getField(), algorithmDepth-1));

				parAlgGameScore.unDo(moves.get(i));
			}
			
			
//			//Invoke
			future = service.invokeAll(list);
			
			//H�chsten suchen
			int counter = 0;
			int column = 0;
			ArrayList<Integer> results = new ArrayList<>();
			
			//In ArrayList �bertragen
			for(Future obj : future){
				results.add((int)obj.get());
			}
			
			//Pr�fe ob im n�chsten Zug verloren wird
			//Ansonsten w�hle h�chsten als neue M�glichkeit
			for (int i = 0; i < results.size(); i++) {
		    	
			    if(results.get(i) >= highest){
			    	
			    	parAlgGameScore.play(moves.get(counter), 'X');
			    	
			    	if(parAlgGameScore.eval() > -95000){
			    	highest = results.get(i);
			    	column = moves.get(counter);
			    	
			    	}
			    	else{
			    		System.out.println("Gef�hrliche Stellung in Spalte erkannt: " + moves.get(counter));
			    	}
			    	parAlgGameScore.unDo(moves.get(counter));
			    	}
			    
			    counter++;
			  }
			
			if(highest == -1000000){
				column = moves.get(0);
				System.out.println("Verloren");
			}
			
			//Laufzeit messen
			final long timeEnd2 = System.currentTimeMillis(); 
	        System.out.println("Laufzeit KI: " + (timeEnd2 - timeStart) + " Millisek.");
			
			//Spalte zur�ck geben
			return column;
		}
		
}
