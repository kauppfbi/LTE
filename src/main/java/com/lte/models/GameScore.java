package  com.lte.models;

import java.util.ArrayList;

public class GameScore {

	//************Klassenvariablen*******************************************
	private char[][] field = new char[7][6];
	
	//************Konstruktoren************************************************
	public GameScore(){
			
	}
	
	public GameScore(char[][] fieldImport){
	
		field = fieldImport;	
		
	}
	
	//*************Methoden*****************************************************
	
		//************Initialisiere*************************************************
		public void initialize(){
			
			//Initialisiert jedes Feld im Spieldfeld mir CHAR '.'
			
			for (int row = 0; row < 6; row++) {		
				for (int column = 0; column < 7; column++) {
					field[column][row] = '.';
				}
				}
			
		}
		
		//*************Print***********************************************************
		public void print(){
			
			//Gebe das Spielfeld in der Konsole aus
			//rown werden gedreht
			
		for (int row = 5; row > -1; row--) {		
		for (int column = 0; column < 7; column++) {
			System.out.print(" " + field[column][row] + " ");
		}
		System.out.println("");
		}
			
		}
		
		//*************Spiele***************************************************************
		public char[][] play(int arraycolumn, char playerCharacter) throws Exception{
			
			//Nehme das Spielfeld aus dem Importparameter und spiele die angegebene column für den Spieler
			//Werfe eine Ausnahme, wenn der Spielzug nicht möglich ist
			
			//Werfe den Chip auf die unterste leere Position in der column
			for (int row = 0; row < 7; row++) {
				if(row == 6){
					//Es steht kein freier Slot mehr zur Verfügung
					throw new IllegalArgumentException("column ist voll");
				}
				//Wenn ein freier Slot gefunden wurde, dann platziere das Zeichen des entsprechenden Spielers
				else if (field[arraycolumn][row] == '.') {
					field[arraycolumn][row] = playerCharacter;
					break;
				}

			}
			
			return field;
		}
		
		public char[][] getField(){
			char[][] deepField = new char[7][6];
			
			for (int row = 0; row < 6; row++) {		
				for (int column = 0; column < 7; column++) {
					deepField[column][row] = field[column][row];
				}
				}
			
			return deepField;
		}
		
		//*************Mache Zug rückgängig***************************************************************
		public char[][] reDo(int arraycolumn) throws Exception{
			
			//Nehme  die angegebene column und mache den letzten Zug rückgängig
			//Werfe eine Ausnahme, wenn der Spielzug nicht möglich ist
			
			//Werfe den Chip auf die unterste leere Position in der column
			for (int row = 5; row > -1; row--) {
				if(row < -1){
					//Es steht kein Zeichen mehr zur Verfügung
					throw new IllegalArgumentException("column ist leer");
				}
				//Wenn ein Spielerzeichen gefunden wurde, dann lösche dieses
				if (field[arraycolumn][row] == 'X' || field[arraycolumn][row] ==  'O') {
					field[arraycolumn][row] = '.';
					break;
				}

			}
			
			return field;
		}
		
		
		//*************Mögliche Züge**********************************************************
		public ArrayList<Integer> possibleMoves(){
				
				ArrayList<Integer> moves = new ArrayList<>();
					
					for (int column = 0; column < 7; column++) {
							if(field[column][5] == '.'){
							moves.add(column);
						}
					}
				
			
			return moves;
			
		}
		
		// ***************Gebe gewonnene Kombination zurÃ¼ck*********
		public int[][] winWhere() {
			int[][] winWhere = new int[4][2];

			winWhere[0][0] = 0;
			winWhere[0][1] = 0;
			winWhere[1][0] = 0;
			winWhere[1][1] = 0;
			winWhere[2][0] = 0;
			winWhere[2][1] = 0;
			winWhere[3][0] = 0;
			winWhere[3][1] = 0;

			// horizantale Moeglichkeiten (24)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 6; row++) {
					int rating = 0;
					rating = evalFunction(field[column][row], field[column + 1][row],
							field[column + 2][row], field[column + 3][row]);
					if (rating == 100000 || rating == -100000) {
						winWhere[0][0] = column;
						winWhere[0][1] = row;
						winWhere[1][0] = column + 1;
						winWhere[1][1] = row;
						winWhere[2][0] = column + 2;
						winWhere[2][1] = row;
						winWhere[3][0] = column + 3;
						winWhere[3][1] = row;
						return winWhere;
					}
				}
			}

			// Vertikale Moeglichkeiten (24)
			for (int column = 0; column < 7; column++) {
				for (int row = 0; row < 3; row++) {
					int rating = 0;
					rating = evalFunction(field[column][row], field[column][row + 1],
							field[column][row + 2], field[column][row + 3]);
					if (rating == 100000 || rating == -100000) {
						winWhere[0][0] = column;
						winWhere[0][1] = row;
						winWhere[1][0] = column;
						winWhere[1][1] = row + 1;
						winWhere[2][0] = column;
						winWhere[2][1] = row + 2;
						winWhere[3][0] = column;
						winWhere[3][1] = row + 3;
						return winWhere;
					}
				}
			}

			// Unten Links -> Oben Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 3; row++) {
					int rating = 0;
					rating = evalFunction(field[column][row], field[column + 1][row + 1],
							field[column + 2][row + 2], field[column + 3][row + 3]);
					if (rating == 100000 || rating == -100000) {
						winWhere[0][0] = column;
						winWhere[0][1] = row;
						winWhere[1][0] = column + 1;
						winWhere[1][1] = row + 1;
						winWhere[2][0] = column + 2;
						winWhere[2][1] = row + 2;
						winWhere[3][0] = column + 3;
						winWhere[3][1] = row + 3;
						return winWhere;
					}
				}
			}

			// Oben Links -> Unten Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 5; row > 2; row--) {
					int rating = 0;
					rating = evalFunction(field[column][row], field[column + 1][row - 1],
							field[column + 2][row - 2], field[column + 3][row - 3]);
					if (rating == 100000 || rating == -100000) {
						winWhere[0][0] = column;
						winWhere[0][1] = row;
						winWhere[1][0] = column + 1;
						winWhere[1][1] = row - 1;
						winWhere[2][0] = column + 2;
						winWhere[2][1] = row - 2;
						winWhere[3][0] = column + 3;
						winWhere[3][1] = row - 3;
						return winWhere;
					}
				}
			}

			return winWhere;
		}
		
		//****************Ist das Spiel gewonnen?*******************************************
		public char isWon(){
			char isGewonnen = 'N';
			
			
			//horizantale Möglichkeiten (24)
			for(int column = 0; column < 4; column++){
				for(int row = 0; row < 6; row++){
					if(field[column][row] == '.'){break;}
					int rating = 0;
					rating = evalFunction(field[column][row], field[column+1][row], field[column+2][row], field[column+3][row]);
					if(rating == 100000){return 'X';}else if(rating == -100000){return 'O';}
					}
				}
			
			
			//Vertikale Möglichkeiten (24)
			for(int column = 0; column < 7; column++){
				for(int row = 0; row < 3; row++){
					if(field[column][row] == '.'){break;}
					int rating = 0;
					rating = evalFunction(field[column][row], field[column][row+1], field[column][row+2], field[column][row+3]);
						if(rating == 100000){return 'X';}else if(rating == -100000){return 'O';}
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 3; row++) {
					if(field[column][row] == '.'){break;}
					int rating = 0;
					rating = evalFunction(field[column][row], field[column+1][row+1], field[column+2][row+2], field[column+3][row+3]);
					if(rating == 100000){return 'X';}else if(rating == -100000){return 'O';}
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 5; row > 2; row--) {
					int rating = 0;
					rating = evalFunction(field[column][row], field[column+1][row-1], field[column+2][row-2], field[column+3][row-3]);
					if(rating == 100000){return 'X';}else if(rating == -100000){return 'O';}
				}
			}
			
			
			
			
			return isGewonnen;
		}
		
		//*********Bewerte Spielstand*********************************************
		//Insgesamt 72 mögliche 4er Kombinationen
		public int eval(){
			int ratingGesamt = 0;
			
			//horizantale Möglichkeiten (24)
			for(int column = 0; column < 4; column++){
				for(int row = 0; row < 6; row++){
					ratingGesamt = ratingGesamt + evalFunction(field[column][row], field[column+1][row], field[column+2][row], field[column+3][row]);
				}
			}
			
			//Vertikale Möglichkeiten (24)
			for(int column = 0; column < 7; column++){
				for(int row = 0; row < 3; row++){
					ratingGesamt = ratingGesamt + evalFunction(field[column][row], field[column][row+1], field[column][row+2], field[column][row+3]);
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 3; row++) {
					ratingGesamt = ratingGesamt + evalFunction(field[column][row], field[column+1][row+1], field[column+2][row+2], field[column+3][row+3]);
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 5; row > 2; row--) {
					ratingGesamt = ratingGesamt + evalFunction(field[column][row], field[column+1][row-1], field[column+2][row-2], field[column+3][row-3]);
				}
			}
			
			return ratingGesamt;
		}
		
		//*************ratingsfunktion***************************************
		public int evalFunction(char eins, char zwei, char drei, char vier){
			int rating = 0;
			int agent = 0;
			int gegner = 0;
			
			if(eins == 'X'){agent++;}else if (eins == 'O') {gegner++;}
			if(zwei == 'X'){agent++;}else if (zwei == 'O') {gegner++;}
			if(drei == 'X'){agent++;}else if (drei == 'O') {gegner++;}
			if(vier == 'X'){agent++;}else if (vier == 'O') {gegner++;}
			
			if(agent != 0 && gegner != 0){rating = 0; return rating;}
			
			if(agent == 1){rating = rating + 1; return rating;} 
			else if(agent == 2){rating = rating + 10; return rating;}
			else if(agent == 3){rating = rating + 1000; return rating;}
			else if(agent == 4){rating = rating + 100000; return rating;}
			
			if(gegner == 1){rating = rating -1; return rating;}
			else if(gegner == 2){rating = rating -10; return rating;}
			else if(gegner == 3){rating = rating -1000; return rating;}
			else if(gegner == 4){rating = rating -100000; return rating;}
			
			return rating;
			
		}
		
		public int getRow(int arraycolumn){
			System.out.println("Array column " + arraycolumn);
			for (int row = 5; row > -2; row--) {
				if(row == -1){
					//column ist leer
					throw new IllegalArgumentException("column ist leer");
				}
				if (field[arraycolumn][row] == 'O' || field[arraycolumn][row] == 'X') {
					
					System.out.println("Array row " + row);
					return row;
				}
			}
			return 0;

		}
		
		
		
	}
	
	

