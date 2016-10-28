package com.lte.aiPar;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.lte.models.Spielstand;

public class Algorithmus implements Callable<Integer>{
	
	//*********Klassenvariablen****************************
	private int gewuenschtetiefe;
	private int gespeicherterZug;
	private Spielstand pufferSpielstand;
	
	//********Konstruktoren********************************
	public Algorithmus(char[][] spielfeld, int algorithmusTiefe){
	
	//Ereuge neuen Spielstand f�r den Algorithmus
	pufferSpielstand = new Spielstand(spielfeld);
	
	//Gew�nschte Tiefe
	gewuenschtetiefe = algorithmusTiefe;

	
	}
	
	//*******Methoden**************************************
	
	
	public int max(int tiefe, int alpha, int beta) {
		char isGewonnen = pufferSpielstand.isGewonnen();
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
	    if (tiefe == 0 || moeglicheZuege.size() == 0 || isGewonnen != 'N'){
	    	if(isGewonnen != 'N'){
	    		return (pufferSpielstand.eval() * ((tiefe + 1)) * 100);
	    	}else{
		    	return pufferSpielstand.eval();
	    	}
	    }
	    int maxWert = alpha;
	    for (int i = 0; i < moeglicheZuege.size(); i++)
	    {
	       try {
			pufferSpielstand.spiele(moeglicheZuege.get(i), 'X');
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       int wert = min(tiefe-1, maxWert, beta);
	       try {
			pufferSpielstand.reDo(moeglicheZuege.get(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       if (wert > maxWert) {
	          maxWert = wert;
	          if (maxWert >= beta)
	             break;
	          if (tiefe == gewuenschtetiefe)
	             gespeicherterZug = moeglicheZuege.get(i);
	       }
	    }
	    return maxWert;
	 }
	
	
	 public int min(int tiefe, int alpha, int beta) {
		char isGewonnen = pufferSpielstand.isGewonnen();
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
	    if (tiefe == 0 || moeglicheZuege.size() == 0 || isGewonnen != 'N'){
	    	if(isGewonnen != 'N'){
	    		return (pufferSpielstand.eval() * ((tiefe + 1)) * 100);
	    	}else{
		    	return pufferSpielstand.eval();
	    	}
	    }
	    int minWert = beta;
	    for (int i = 0; i < moeglicheZuege.size(); i++)
	    {
	    	try {
				pufferSpielstand.spiele(moeglicheZuege.get(i), 'O');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int wert = max(tiefe-1, alpha, minWert);
	       try {
				pufferSpielstand.reDo(moeglicheZuege.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       if (wert < minWert) {
	          minWert = wert;
	          if (minWert <= alpha)
	             break;
	       }
	    }
	    return minWert;
	 }

	@Override
	public Integer call() throws Exception {

			int min = min(gewuenschtetiefe, -1000000, 1000000);					
			return min;
	}
	
	

}
