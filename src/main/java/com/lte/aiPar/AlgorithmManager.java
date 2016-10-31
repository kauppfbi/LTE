package com.lte.aiPar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.lte.models.GameScore;


/**
 * The AlgortihmManager coordinates the usage of different algorithms, tactics and algorithm depths. It represents the AI.
 * @author Fabian Soelker
 *
 */
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
		 * @param playerByte character of own player
		 * @param opponent character of opponent
		 * @return best move as integer
		 * @throws Exception
		 */
		public int ParallelAlphaBeta(byte[][] field, int algorithmDepth, double calculationTime, byte playerByte) throws Exception{
			
			
			//Laufzeitanalyse starten
			final long timeStart = System.currentTimeMillis(); 
			
			//Arrays lï¿½schen
			list.clear();
			future.clear();
			
			//Hï¿½chstwert und Spalte
			int highest = -1000000;
			
			//Neuen Spielstand aufbauen
			GameScore parAlgGameScore = new GameScore(field);
			
			//Mï¿½gliche Zï¿½ge
			byte[] moves = parAlgGameScore.possibleMoves();
			
			//Die ersten Runden beschleunigen
			roundCounter++;

			if(roundCounter <= 7){
				algorithmDepth = 12;
			}
			
			System.out.println(algorithmDepth);
			
			//Prï¿½fe ob nur noch ein Zug vorhanden ist
			if(moves[1] == 99 && moves[0] != 99)
			{
				return moves[0];
			}
			
			//Prï¿½fe ob in den nï¿½chsten Zuegen gewonnen werden kann
			SingleAlgorithm simpleAlg = new SingleAlgorithm(field, 4);
			int simpleMove = simpleAlg.alphaBeta();
			parAlgGameScore.play(simpleMove, playerByte);
			int eval = parAlgGameScore.eval();
			if (eval > 95000) {
				
				final long timeEnd1 = System.currentTimeMillis(); 
		        System.out.println("Laufzeit 4er: " + (timeEnd1 - timeStart) + " Millisek.");
		        return simpleMove;
		        
			}
			else {
				parAlgGameScore.unDo(simpleMove);
			}
			
			
			//Spiele die ersten Zï¿½ge und erzeuge neue Tasks
			for (int i = 0; i < moves.length; i++) {
				if(moves[i] == 99){break;}
				parAlgGameScore.play(moves[i], playerByte);
				
				list.add(new Algorithm(parAlgGameScore.getField(), algorithmDepth-1));

				parAlgGameScore.unDo(moves[i]);
			}
			
			
			//Calc Time
			calculationTime = (calculationTime * 1000) - 300;
			if(calculationTime < 300){calculationTime = 300;}
			
			//Hï¿½chsten suchen
			int counter = 0;
			int column = 0;
			ArrayList<Integer> results = new ArrayList<>();
			
			//Invoke + Timeout
			try{
				future = service.invokeAll(list, (long)calculationTime, TimeUnit.MILLISECONDS);
				
				//In ArrayList ï¿½bertragen
				for(Future<?> obj : future){
					results.add((int)obj.get());
				}
			}
			catch (CancellationException e) {
				SingleAlgorithm timeoutAlg = new SingleAlgorithm(field, 6);
				int timeoutMove = timeoutAlg.alphaBeta();
				System.out.println("Timeout!");
				return timeoutMove;
			}

			
			//Prï¿½fe ob im nï¿½chsten Zug verloren wird
			//Ansonsten wï¿½hle hï¿½chsten als neue Mï¿½glichkeit
			for (int i = 0; i < results.size(); i++) {
		    	
			    if(results.get(i) >= highest){
			    	
			    	parAlgGameScore.play(moves[counter], (byte) 1);
			    	
			    	if(parAlgGameScore.eval() > -95000){
			    	highest = results.get(i);
			    	column = moves[counter];
			    	
			    	}
			    	else{
			    		System.out.println("Gefï¿½hrliche Stellung in Spalte erkannt: " + moves[counter]);
			    	}
			    	parAlgGameScore.unDo(moves[counter]);
			    	}
			    
			    counter++;
			  }
			
			if(highest == -1000000){
				column = moves[0];
				System.out.println("Verloren");
			}
			
			//Laufzeit messen
			final long timeEnd2 = System.currentTimeMillis(); 
	        System.out.println("Laufzeit KI: " + (timeEnd2 - timeStart) + " Millisek.");
			
			//Spalte zurï¿½ck geben
			return column;
		}
		
}
