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
			int hoechster = 0;
			hoechster = -Integer.MAX_VALUE;

			
			//Neuen Spielstand aufbauen
			Spielstand parAlgSpielstand = new Spielstand(spielfeld);
			
			//M�gliche Z�ge
			ArrayList<Integer> zuege = new ArrayList<>();
			zuege = parAlgSpielstand.moeglicheZuege();
			
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
				
				list.add(new Algorithmus(parAlgSpielstand.getSpielfeld(), algorithmusTiefe-1, playerZeichen, gegner));

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
			
			if(hoechster == -Integer.MAX_VALUE){
				spalte = zuege.get(0);
				System.out.println("Verloren");
			}
			
			//Laufzeit messen
			final long timeEnd2 = System.currentTimeMillis(); 
	        System.out.println("Laufzeit: " + (timeEnd2 - timeStart) + " Millisek.");
			
			//Spalte zur�ck geben
			return spalte;
		}
		
		public int tiefeAutomatischWaehlen(double maxLaufzeit) throws Exception{
			
			//Uebertrage double in int Milisek
			int laufzeitMiliSek = (int) (maxLaufzeit * 1000);
			
			//Interiere ueber Moeglichkeiten
			for (int i = 6; i < 20; i++) {
				
				//Erzeuge neuen initialien Spielstand
				Spielstand testSpielstand = new Spielstand();
				testSpielstand.initialisiere();

				//Spiele und pr�fe ob die Zeit passt
				System.out.println("Teste Tiefe " + i);
				final long timeStartTest = System.currentTimeMillis();
				ParallelAlphaBeta(testSpielstand.getSpielfeld(), i, 'X', 'O');
				final long timeEndTest = System.currentTimeMillis();
				
				//Wenn ueberschritten, dann gebe letzten zurueck
		        if((timeEndTest - timeStartTest) > laufzeitMiliSek){
		        	return i-1;
		        }
			}	
			return 20;
		}
}
