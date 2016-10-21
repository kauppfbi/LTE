package  com.lte.models;

import java.util.ArrayList;

public class Spielstand {

	//************Klassenvariablen*******************************************
	private char[][] spielfeld = new char[7][6];
	
	//************Konstruktoren************************************************
	public Spielstand(){
			
	}
	
	public Spielstand(char[][] spielfeldImport){
	
		spielfeld = spielfeldImport;	
		
	}
	
	//*************Methoden*****************************************************
	
		//************Initialisiere*************************************************
		public void initialisiere(){
			
			//Initialisiert jedes Feld im Spieldfeld mir CHAR '.'
			
			for (int zeile = 0; zeile < 6; zeile++) {		
				for (int spalte = 0; spalte < 7; spalte++) {
					spielfeld[spalte][zeile] = '.';
				}
				}
			
		}
		
		//*************Print***********************************************************
		public void print(){
			
			//Gebe das Spielfeld in der Konsole aus
			//Zeilen werden gedreht
			
		for (int zeile = 5; zeile > -1; zeile--) {		
		for (int spalte = 0; spalte < 7; spalte++) {
			System.out.print(" " + spielfeld[spalte][zeile] + " ");
		}
		System.out.println("");
		}
			
		}
		
		//*************Spiele***************************************************************
		public char[][] spiele(int arraySpalte, char playerZeichen) throws Exception{
			
			//Nehme das Spielfeld aus dem Importparameter und spiele die angegebene Spalte für den Spieler
			//Werfe eine Ausnahme, wenn der Spielzug nicht möglich ist
			
			//Werfe den Chip auf die unterste leere Position in der Spalte
			for (int zeile = 0; zeile < 7; zeile++) {
				if(zeile == 6){
					//Es steht kein freier Slot mehr zur Verfügung
					throw new IllegalArgumentException("Spalte ist voll");
				}
				//Wenn ein freier Slot gefunden wurde, dann platziere das Zeichen des entsprechenden Spielers
				if (spielfeld[arraySpalte][zeile] == '.') {
					spielfeld[arraySpalte][zeile] = playerZeichen;
					break;
				}

			}
			
			return spielfeld;
		}
		
		public char[][] getSpielfeld(){
			char[][] deepSpielfeld = new char[7][6];
			
			for (int zeile = 0; zeile < 6; zeile++) {		
				for (int spalte = 0; spalte < 7; spalte++) {
					deepSpielfeld[spalte][zeile] = spielfeld[spalte][zeile];
				}
				}
			
			return deepSpielfeld;
		}
		
		//*************Mache Zug rückgängig***************************************************************
		public char[][] reDo(int arraySpalte) throws Exception{
			
			//Nehme  die angegebene Spalte und mache den letzten Zug rückgängig
			//Werfe eine Ausnahme, wenn der Spielzug nicht möglich ist
			
			//Werfe den Chip auf die unterste leere Position in der Spalte
			for (int zeile = 5; zeile > -1; zeile--) {
				if(zeile < -1){
					//Es steht kein Zeichen mehr zur Verfügung
					throw new IllegalArgumentException("Spalte ist leer");
				}
				//Wenn ein Spielderzeichen gefunden wurde, dann lösche dieses
				if (spielfeld[arraySpalte][zeile] == 'X' || spielfeld[arraySpalte][zeile] ==  'O') {
					spielfeld[arraySpalte][zeile] = '.';
					break;
				}

			}
			
			return spielfeld;
		}
		
		
		//*************Mögliche Züge**********************************************************
		public ArrayList<Integer> moeglicheZuege(){
				
				ArrayList<Integer> zuege = new ArrayList<>();
					
					for (int spalte = 0; spalte < 7; spalte++) {
							if(spielfeld[spalte][5] == '.'){
							zuege.add(spalte);
						}
					}
				
			
			return zuege;
			
		}
		
		// ***************Gebe gewonnene Kombination zurÃ¼ck*********
		public int[][] woGewonnen() {
			int[][] woGewonnen = new int[4][2];

			woGewonnen[0][0] = 0;
			woGewonnen[0][1] = 0;
			woGewonnen[1][0] = 0;
			woGewonnen[1][1] = 0;
			woGewonnen[2][0] = 0;
			woGewonnen[2][1] = 0;
			woGewonnen[3][0] = 0;
			woGewonnen[3][1] = 0;

			// horizantale Moeglichkeiten (24)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 0; zeile < 6; zeile++) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte + 1][zeile],
							spielfeld[spalte + 2][zeile], spielfeld[spalte + 3][zeile]);
					if (bewertung == 100000 || bewertung == -100000) {
						woGewonnen[0][0] = spalte;
						woGewonnen[0][1] = zeile;
						woGewonnen[1][0] = spalte + 1;
						woGewonnen[1][1] = zeile;
						woGewonnen[2][0] = spalte + 2;
						woGewonnen[2][1] = zeile;
						woGewonnen[3][0] = spalte + 3;
						woGewonnen[3][1] = zeile;
						return woGewonnen;
					}
				}
			}

			// Vertikale Moeglichkeiten (24)
			for (int spalte = 0; spalte < 7; spalte++) {
				for (int zeile = 0; zeile < 3; zeile++) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte][zeile + 1],
							spielfeld[spalte][zeile + 2], spielfeld[spalte][zeile + 3]);
					if (bewertung == 100000 || bewertung == -100000) {
						woGewonnen[0][0] = spalte;
						woGewonnen[0][1] = zeile;
						woGewonnen[1][0] = spalte;
						woGewonnen[1][1] = zeile + 1;
						woGewonnen[2][0] = spalte;
						woGewonnen[2][1] = zeile + 2;
						woGewonnen[3][0] = spalte;
						woGewonnen[3][1] = zeile + 3;
						return woGewonnen;
					}
				}
			}

			// Unten Links -> Oben Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 0; zeile < 3; zeile++) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte + 1][zeile + 1],
							spielfeld[spalte + 2][zeile + 2], spielfeld[spalte + 3][zeile + 3]);
					if (bewertung == 100000 || bewertung == -100000) {
						woGewonnen[0][0] = spalte;
						woGewonnen[0][1] = zeile;
						woGewonnen[1][0] = spalte + 1;
						woGewonnen[1][1] = zeile + 1;
						woGewonnen[2][0] = spalte + 2;
						woGewonnen[2][1] = zeile + 2;
						woGewonnen[3][0] = spalte + 3;
						woGewonnen[3][1] = zeile + 3;
						return woGewonnen;
					}
				}
			}

			// Oben Links -> Unten Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 5; zeile > 2; zeile--) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte + 1][zeile - 1],
							spielfeld[spalte + 2][zeile - 2], spielfeld[spalte + 3][zeile - 3]);
					if (bewertung == 100000 || bewertung == -100000) {
						woGewonnen[0][0] = spalte;
						woGewonnen[0][1] = zeile;
						woGewonnen[1][0] = spalte + 1;
						woGewonnen[1][1] = zeile - 1;
						woGewonnen[2][0] = spalte + 2;
						woGewonnen[2][1] = zeile - 2;
						woGewonnen[3][0] = spalte + 3;
						woGewonnen[3][1] = zeile - 3;
						return woGewonnen;
					}
				}
			}

			return woGewonnen;
		}
		
		//****************Ist das Spiel gewonnen?*******************************************
		public char isGewonnen(){
			char isGewonnen = 'N';
			
			
			//horizantale Möglichkeiten (24)
			for(int spalte = 0; spalte < 4; spalte++){
				for(int zeile = 0; zeile < 6; zeile++){
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile], spielfeld[spalte+2][zeile], spielfeld[spalte+3][zeile]);
					if(bewertung == 100000){return 'X';}else if(bewertung == -100000){return 'O';}
					else if(bewertung == 0){break;}	
					}
				}
			
			
			//Vertikale Möglichkeiten (24)
			for(int spalte = 0; spalte < 7; spalte++){
				for(int zeile = 0; zeile < 3; zeile++){
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte][zeile+1], spielfeld[spalte][zeile+2], spielfeld[spalte][zeile+3]);
						if(bewertung == 100000){return 'X';}else if(bewertung == -100000){return 'O';}
						else if(bewertung == 0){break;}
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 0; zeile < 3; zeile++) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile+1], spielfeld[spalte+2][zeile+2], spielfeld[spalte+3][zeile+3]);
					if(bewertung == 100000){return 'X';}else if(bewertung == -100000){return 'O';}
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 5; zeile > 2; zeile--) {
					int bewertung = 0;
					bewertung = evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile-1], spielfeld[spalte+2][zeile-2], spielfeld[spalte+3][zeile-3]);
					if(bewertung == 100000){return 'X';}else if(bewertung == -100000){return 'O';}
				}
			}
			
			
			
			
			return isGewonnen;
		}
		
		//*********Bewerte Spielstand*********************************************
		//Insgesamt 72 mögliche 4er Kombinationen
		public int eval(){
			int bewertungGesamt = 0;
			
			//horizantale Möglichkeiten (24)
			for(int spalte = 0; spalte < 4; spalte++){
				for(int zeile = 0; zeile < 6; zeile++){
					bewertungGesamt = bewertungGesamt + evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile], spielfeld[spalte+2][zeile], spielfeld[spalte+3][zeile]);
				}
			}
			
			//Vertikale Möglichkeiten (24)
			for(int spalte = 0; spalte < 7; spalte++){
				for(int zeile = 0; zeile < 3; zeile++){
					bewertungGesamt = bewertungGesamt + evalFunction(spielfeld[spalte][zeile], spielfeld[spalte][zeile+1], spielfeld[spalte][zeile+2], spielfeld[spalte][zeile+3]);
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 0; zeile < 3; zeile++) {
					bewertungGesamt = bewertungGesamt + evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile+1], spielfeld[spalte+2][zeile+2], spielfeld[spalte+3][zeile+3]);
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int spalte = 0; spalte < 4; spalte++) {
				for (int zeile = 5; zeile > 2; zeile--) {
					bewertungGesamt = bewertungGesamt + evalFunction(spielfeld[spalte][zeile], spielfeld[spalte+1][zeile-1], spielfeld[spalte+2][zeile-2], spielfeld[spalte+3][zeile-3]);
				}
			}
			
			return bewertungGesamt;
		}
		
		//*************Bewertungsfunktion***************************************
		public int evalFunction(char eins, char zwei, char drei, char vier){
			int bewertung = 0;
			int agent = 0;
			int gegner = 0;
			
			if(eins == 'X'){agent++;}else if (eins == 'O') {gegner++;}
			if(zwei == 'X'){agent++;}else if (zwei == 'O') {gegner++;}
			if(drei == 'X'){agent++;}else if (drei == 'O') {gegner++;}
			if(vier == 'X'){agent++;}else if (vier == 'O') {gegner++;}
			
			if(agent != 0 && gegner != 0){bewertung = 0; return bewertung;}
			
			if(agent == 1){bewertung = bewertung + 1; return bewertung;} 
			else if(agent == 2){bewertung = bewertung + 10; return bewertung;}
			else if(agent == 3){bewertung = bewertung + 1000; return bewertung;}
			else if(agent == 4){bewertung = bewertung + 100000; return bewertung;}
			
			if(gegner == 1){bewertung = bewertung -1; return bewertung;}
			else if(gegner == 2){bewertung = bewertung -10; return bewertung;}
			else if(gegner == 3){bewertung = bewertung -1000; return bewertung;}
			else if(gegner == 4){bewertung = bewertung -100000; return bewertung;}
			
			return bewertung;
			
		}
		
		
		
	}
	
	

