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
	/**
	 * Initializes all slots of the corresponding field of the game score with the initial character '.'
	 */
		public void initialize(){
			
			//Initialisiert jedes Feld im Spieldfeld mir CHAR '.'
			
			for (int row = 0; row < 6; row++) {		
				for (int column = 0; column < 7; column++) {
					field[column][row] = '.';
				}
				}
			
		}
		
		//*************Print***********************************************************
		/**
		 * returns the current field in the console
		 */
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
		/**
		 * plays a move for for a given player and a column in the given field
		 * @param arraycolumn column for the move on the field
		 * @param playerCharacter character for the given move. 'O' or 'X'.
		 * @return new field with imported move
		 * @throws Exception move is not possible
		 */
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
		/**
		 * returns hard copy of a field
		 * @return hard copy field[][]
		 */
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
		/**
		 * Undo the last move in a given column
		 * @param arraycolumn column for the move
		 * @return new field without given move in given column
		 * @throws Exception column is empty
		 */
		public char[][] unDo(int arraycolumn) throws Exception{
			
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
		/**
		 * Return possible moves for the current field of the game score
		 * @return ArrayList<Integer> with possible moves
		 */
		public int[] possibleMoves(){
				
				int[] moves = new int[7];
				int counter = 0;
					
					for (int column = 0; column < 7; column++) {
							if(field[column][5] == '.'){
							moves[counter] = column;
							counter++;
						}
					}
					for (int i = counter; i < 7; i++) {
						moves[i] = 99;
					}
				
			
			return moves;
			
		}
		
		// ***************Gebe gewonnene Kombination zurÃ¼ck*********
		/**
		 * returns the winning combination of the current field of the game score
		 * @return int[4][2] with [column][row] 
		 */
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
		/**
		 * Returns if the current game score is won by one player
		 * @return char 'N' for no winner. 'X' for the own player and 'O' for the opponent.
		 */
		public char isWon(){
			char isGewonnen = 'N';
			
			
			//horizantale Möglichkeiten (24)
			for(int column = 0; column < 4; column++){
				for(int row = 0; row < 6; row++){
					if(field[column][row] == '.'){break;}					
					if(field[column][row] == 'X' && field[column+1][row] == 'X' &&  field[column+2][row] == 'X' &&  field[column+3][row] == 'X'){return 'X';}
					if(field[column][row] == 'O' && field[column+1][row] == 'O' &&  field[column+2][row] == 'O' &&  field[column+3][row] == 'O'){return 'O';}
					}
				}
			
			
			//Vertikale Möglichkeiten (24)
			for(int column = 0; column < 7; column++){
				for(int row = 0; row < 3; row++){
					if(field[column][row] == '.'){break;}
						if(field[column][row] == 'X' &&  field[column][row+1] == 'X' &&  field[column][row+2] == 'X' &&  field[column][row+3] == 'X'){return 'X';}
						if(field[column][row] == 'O' &&  field[column][row+1] == 'O' &&  field[column][row+2] == 'O' &&  field[column][row+3] == 'O'){return 'O';}
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 3; row++) {
					if(field[column][row] == '.'){break;}
					
					if(field[column][row] == 'X' && field[column+1][row+1] == 'X' && field[column+2][row+2] == 'X' && field[column+3][row+3] == 'X'){return 'X';}
					if(field[column][row] == 'O' && field[column+1][row+1] == 'O' && field[column+2][row+2] == 'O' && field[column+3][row+3] == 'O'){return 'O';}
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 5; row > 2; row--) {
					if(field[column][row] == '.'){break;}
					
					if(field[column][row] == 'X' && field[column+1][row-1] == 'X' && field[column+2][row-2] == 'X' && field[column+3][row-3] == 'X'){return 'X';}
					if(field[column][row] == 'O' && field[column+1][row-1] == 'O' && field[column+2][row-2] == 'O' && field[column+3][row-3] == 'O'){return 'O';}
				}
			}
			
			
			
			
			return isGewonnen;
		}
		
		//*********Bewerte Spielstand*********************************************
		//Insgesamt 72 mögliche 4er Kombinationen
		/**
		 * Return a rating for the current field of the game score
		 * @return returns an overall rating as integer
		 */
		public int eval(){
			int ratingOverall = 0;
			
			//horizantale Möglichkeiten (24)
			for(int column = 0; column < 4; column++){
				for(int row = 0; row < 6; row++){
					if(field[column][row] == '.'){break;}
					ratingOverall = ratingOverall + evalFunction(field[column][row], field[column+1][row], field[column+2][row], field[column+3][row]);
				}
			}
			
			//Vertikale Möglichkeiten (24)
			for(int column = 0; column < 7; column++){
				for(int row = 0; row < 3; row++){
					if(field[column][row] == '.'){break;}
					ratingOverall = ratingOverall + evalFunction(field[column][row], field[column][row+1], field[column][row+2], field[column][row+3]);
				}
			}
			
			//Unten Links -> Oben Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 0; row < 3; row++) {
					if(field[column][row] == '.'){break;}
					ratingOverall = ratingOverall + evalFunction(field[column][row], field[column+1][row+1], field[column+2][row+2], field[column+3][row+3]);
				}
			}
			
			//Oben Links -> Unten Rechts (12)
			for (int column = 0; column < 4; column++) {
				for (int row = 5; row > 2; row--) {
					ratingOverall = ratingOverall + evalFunction(field[column][row], field[column+1][row-1], field[column+2][row-2], field[column+3][row-3]);
				}
			}
			
			return ratingOverall;
		}
		
		//*************ratingsfunktion***************************************
		/**
		 * Returns a rating for four given characters. Is called by eval()
		 * @param one first character
		 * @param two second character
		 * @param three third character
		 * @param four fourth character
		 * @return returns a rating as integer
		 */
		private int evalFunction(char one, char two, char three, char four){
			int rating = 0;
			int agent = 0;
			int opponent = 0;
			
			if(one == 'X'){agent++;}else if (one == 'O') {opponent++;}
			if(two == 'X'){agent++;}else if (two == 'O') {opponent++;}
			if(three == 'X'){agent++;}else if (three == 'O') {opponent++;}
			if(four == 'X'){agent++;}else if (four == 'O') {opponent++;}
			
			if(agent != 0 && opponent != 0){rating = 0; return rating;}
			
			if(agent == 1){rating = rating + 1; return rating;} 
			else if(agent == 2){rating = rating + 10; return rating;}
			else if(agent == 3){rating = rating + 1000; return rating;}
			else if(agent == 4){rating = rating + 100000; return rating;}
			
			if(opponent == 1){rating = rating -1; return rating;}
			else if(opponent == 2){rating = rating -10; return rating;}
			else if(opponent == 3){rating = rating -1000; return rating;}
			else if(opponent == 4){rating = rating -100000; return rating;}
			
			return rating;
			
		}
		/**
		 * Returns the row of the highest stone in the given column
		 * @param arraycolumn column for the stone
		 * @return returns column as integer
		 */
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
	
	

