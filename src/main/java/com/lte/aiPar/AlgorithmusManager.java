package com.lte.aiPar;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lte.models.Spielstand;

public class AlgorithmusManager {

	//************Klassenvariablen*******************************************
		final ExecutorService service;
		List<Callable<Integer>> list = new ArrayList<> ();
		List<Future<Integer>> future = new ArrayList<> ();
		int roundCounter = 0;
		
		//************Konstruktoren************************************************
		public AlgorithmusManager() {
			service = Executors.newFixedThreadPool(7); 
				
		}	
			
		
		
		//*************Methoden*****************************************************

		//******Shutdown********************************
		public void shutdown(){
			service.shutdownNow();
		}
		
		//******Alpha Beta**************************************
		public int ParallelAlphaBeta(char[][] spielfeld, int algorithmusTiefe, char playerZeichen, char gegner) throws Exception{
			
			
			//Laufzeitanalyse starten
			final long timeStart = System.currentTimeMillis(); 
			
			//Arrays l�schen
			list.clear();
			future.clear();
			
			//H�chstwert und Spalte
			int hoechster = -1000000;
			
			//Neuen Spielstand aufbauen
			Spielstand parAlgSpielstand = new Spielstand(spielfeld);
			
			//M�gliche Z�ge
			ArrayList<Integer> zuege = new ArrayList<>();
			zuege = parAlgSpielstand.moeglicheZuege();
			
			//Die ersten Runden beschleunigen
			roundCounter++;

			if(roundCounter <= 7){
				algorithmusTiefe = 8;
			}
			
			System.out.println(algorithmusTiefe);
			
			//Pr�fe ob nur noch ein Zug vorhanden ist
			if(zuege.size() == 1)
			{
				return zuege.get(0);
			}
			
			//Pr�fe ob in den n�chsten Zuegen gewonnen werden kann
			SingleAlgorithmus simpleAlg = new SingleAlgorithmus(spielfeld, 4);
			int simpleZug = simpleAlg.alphaBeta();
			parAlgSpielstand.spiele(simpleZug, playerZeichen);
			int eval = parAlgSpielstand.eval();
			if (eval > 95000) {
				
				final long timeEnd1 = System.currentTimeMillis(); 
		        System.out.println("Laufzeit 4er: " + (timeEnd1 - timeStart) + " Millisek.");
		        return simpleZug;
		        
			}
			else {
				parAlgSpielstand.reDo(simpleZug);
			}
			
			
			//Spiele die ersten Z�ge und erzeuge neue Tasks
			for (int i = 0; i < zuege.size(); i++) {
				parAlgSpielstand.spiele(zuege.get(i), playerZeichen);
				
				list.add(new Algorithmus(parAlgSpielstand.getSpielfeld(), algorithmusTiefe-1));

				parAlgSpielstand.reDo(zuege.get(i));
			}
			
			
//			//Invoke
			future = service.invokeAll(list);
			
			//H�chsten suchen
			int counter = 0;
			int spalte = 0;
			ArrayList<Integer> ergebnisse = new ArrayList<>();
			
			//In ArrayList �bertragen
			for(Future obj : future){
				ergebnisse.add((int)obj.get());
			}
			
			//Pr�fe ob im n�chsten Zug verloren wird
			//Ansonsten w�hle h�chsten als neue M�glichkeit
			for (int i = 0; i < ergebnisse.size(); i++) {
		    	
			    if(ergebnisse.get(i) >= hoechster){
			    	
			    	parAlgSpielstand.spiele(zuege.get(counter), 'X');
			    	
			    	if(parAlgSpielstand.eval() > -95000){
			    	hoechster = ergebnisse.get(i);
			    	spalte = zuege.get(counter);
			    	
			    	}
			    	else{
			    		System.out.println("Gef�hrliche Stellung in Spalte erkannt: " + zuege.get(counter));
			    	}
			    	parAlgSpielstand.reDo(zuege.get(counter));
			    	}
			    
			    counter++;
			  }
			
			if(hoechster == -1000000){
				spalte = zuege.get(0);
				System.out.println("Verloren");
			}
			
			//Laufzeit messen
			final long timeEnd2 = System.currentTimeMillis(); 
	        System.out.println("Laufzeit KI: " + (timeEnd2 - timeStart) + " Millisek.");
			
			//Spalte zur�ck geben
			return spalte;
		}
		
}
