package com.lte.aiPar;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.lte.models.Spielstand;

public class Algorithmus implements Callable<Integer>{
	
	//*********Klassenvariablen****************************
	private int gewuenschtetiefe;
	private int gespeicherterZug;
	private char playerZeichen;
	private char gegner;
	private Spielstand pufferSpielstand;
	
	//********Konstruktoren********************************
	public Algorithmus(char[][] spielfeld, int algorithmusTiefe, char playerZeichen, char gegner){
	
	//Ereuge neuen Spielstand f�r den Algorithmus
	pufferSpielstand = new Spielstand(spielfeld);
	
	//Gew�nschte Tiefe
	gewuenschtetiefe = algorithmusTiefe;
	
	//Player
	this.gegner = gegner;
	this.playerZeichen = playerZeichen;
	}
	
	//*******Methoden**************************************
	
	
	public int max(int tiefe, int alpha, int beta, char playerZeichen, char gegner) {
		char isGewonnen = pufferSpielstand.isGewonnen();
	    if (tiefe == 0 || pufferSpielstand.moeglicheZuege().size() == 0 || isGewonnen != 'N'){
	    	if(isGewonnen != 'N'){
	    		return (pufferSpielstand.eval() * ((tiefe + 1)) * 100);
	    	}else{
		    	return pufferSpielstand.eval();
	    	}
	    }
	    int maxWert = alpha;
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
	    for (int i = 0; i < moeglicheZuege.size(); i++)
	    {
	       try {
			pufferSpielstand.spiele(moeglicheZuege.get(i), playerZeichen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       int wert = min(tiefe-1, maxWert, beta, playerZeichen, gegner);
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
	
	
	 public int min(int tiefe, int alpha, int beta, char playerZeichen, char gegner) {
		char isGewonnen = pufferSpielstand.isGewonnen();
	    if (tiefe == 0 || pufferSpielstand.moeglicheZuege().size() == 0 || isGewonnen != 'N'){
	    	if(isGewonnen != 'N'){
	    		return (pufferSpielstand.eval() * ((tiefe + 1)) * 100);
	    	}else{
		    	return pufferSpielstand.eval();
	    	}
	    }
	    int minWert = beta;
	    ArrayList<Integer> moeglicheZuege = pufferSpielstand.moeglicheZuege();
	    for (int i = 0; i < moeglicheZuege.size(); i++)
	    {
	    	try {
				pufferSpielstand.spiele(moeglicheZuege.get(i), gegner);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	       int wert = max(tiefe-1, alpha, minWert, playerZeichen, gegner);
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

			int min = min(gewuenschtetiefe, -Integer.MAX_VALUE, Integer.MAX_VALUE, playerZeichen, gegner);					
			return min;
	}
	
	

}
