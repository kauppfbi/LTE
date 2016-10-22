package com.lte.aiPar;


import java.util.ArrayList;

import com.lte.models.Spielstand;

public class SingleAlgorithmus{
	
	//*********Klassenvariablen****************************
	private int gewuenschtetiefe;
	private int gespeicherterZug;
	private Spielstand pufferSpielstand;
	
	//********Konstruktoren********************************
	public SingleAlgorithmus(char[][] spielfeld, int algorithmusTiefe){
	
	//Ereugen neuen Spielstand f�r den Algorithmus
	this.pufferSpielstand = new Spielstand(spielfeld);
	
	//Gew�nschte Tiefe
	this.gewuenschtetiefe = algorithmusTiefe;
	}
	
	//*******Methoden**************************************
	
	public  int alphaBeta(){ 
       
		max(gewuenschtetiefe, -Integer.MAX_VALUE, Integer.MAX_VALUE);
		

		return gespeicherterZug;
	}
	
	public  int max(int tiefe, int alpha, int beta) {
	    if (tiefe == 0){
	       return pufferSpielstand.eval();
	    }
	    int maxWert = alpha;
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
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
	
	
	 public  int min(int tiefe, int alpha, int beta) {
	    if (tiefe == 0 || pufferSpielstand.moeglicheZuege().size() == 0){
	    	return pufferSpielstand.eval();
	    }
	    int minWert = beta;
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
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
	
	

}