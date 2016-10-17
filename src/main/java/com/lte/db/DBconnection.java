package com.lte.db;

import java.sql.*;

public class DBconnection {

	// Create db connection and statement object
	// Available for all methods
	public Connection con = null;
	public Statement stmt = null;
	
	public DBconnection() {
		
		// load jdbc driver
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Database driver class not found");
		}
		
		// Connect connection object to file db
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:db/game;shutdown=true;hsqldb.write_delay=false;", "root", "root");
			System.out.println("LOG: created db connection");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Inserts a new player into the database and checks if the user already exists
	 * @param name
	 * @return playerid of existing or new player
	 */
	private int insertPlayer(String name) {
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't create statement");
		}
		
		String sql = "INSERT INTO \"PUBLIC\".\"PLAYER\" (\"NAME\" ) VALUES ('" + name + "')";
		
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: created new player entry in db table PLAYER");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't create new player in db");
		}
		
		sql = "SELECT max(PLAYERID) from PUBLIC.PLAYER";
		int id = 0;
		
		try {
			
			ResultSet res = stmt.executeQuery(sql);
			System.out.println("LOG: got new playerid from db table PLAYER");
			
			if(res.next()) {
				id = res.getInt(1);
			}
			res.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;
	}
	
	/**
	 * Creates all database entries needed for a new game. This is the first method to call if you want to start a new game.
	 * @param opponentName
	 * @param startingPlayer
	 * @return integer array with game information <br>
	 * array[0] = gameid <br>
	 * array[1] = setid <br>
	 * array[2] = opponentid
	 * 
	 */
	public int[] startNewGame(String opponentName, String startingPlayer) {
				
		int opponentID = 0;
		int gameID = 0;
		int setID = 0;
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create player entry or get id of existing player entry		
		String sql = "SELECT * FROM \"PUBLIC\".\"PLAYER\" WHERE NAME = '" + opponentName + "'";
		
		try {
			ResultSet res = stmt.executeQuery(sql);
			
			if (!res.isBeforeFirst() ) {    
			    System.out.println("LOG: player not found, creating new player"); 
			    opponentID = insertPlayer(opponentName);
			}else{
				System.out.println("LOG: player already existed, skipping creation");
				if(res.next()) {
					opponentID = res.getInt(1);
				}
				res.close();
			}			
		} catch (SQLException e) {
			System.out.println("Couldn't create new player or get playerid of existing player");
		}
		
		// create new game entry with playerid as opponent, current time and no values for points and winner
		sql = "INSERT INTO \"PUBLIC\".\"GAME\" ( \"OPPONENTID\", \"POINTSOWN\", \"POINTSOPPONENT\", \"WINNER\" ) VALUES ( " + opponentID + ", null, null, '')";
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: Created game entry in db table");
		} catch (SQLException e) {
			System.out.println("Couldn't create new game");
		}
		
		sql = "SELECT max(GAMEID) from PUBLIC.GAME";
		try {
			
			ResultSet res = stmt.executeQuery(sql);
			System.out.println("LOG: got gameid of newly created game");
			if(res.next()) {
				gameID = res.getInt(1);
			}
			res.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create 1st gameset referencing the id of the newly created game, starting player O or X
		sql = "INSERT INTO \"PUBLIC\".\"GAMESET\" ( \"GAMEID\", \"STARTINGPLAYER\" ) VALUES ( " + gameID + ", '" + startingPlayer + "')";
		
		try {
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "SELECT max(SETID) from PUBLIC.GAMESET";
		try {
			
			ResultSet res = stmt.executeQuery(sql);
			
			if(res.next()) {
				setID = res.getInt(1);
			}
			res.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int gameInfo[] = new int[3];
		gameInfo[0] = gameID;
		gameInfo[1] = setID;
		gameInfo[2] = opponentID;
		
		return gameInfo;
	}
	
	/**
	 * Creates new entry in the database for a new set after the previous ended. Updates the game entry in the database with the score after the previous set.
	 * @param iGameID
	 * @param GamePointsOwn
	 * @param GamePointsOpponent
	 * @return ID of new gameset
	 */
	public int createNewSet(int iGameID, int GamePointsOwn, int GamePointsOpponent) {
		// Set starting player to opposite player than set before
		// Update game entry with latest score
		
		int setID = 0;
		int latestSetID = 0;
		String newStartingPlayer = "";
		String lastStartingPlayer = "";
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get latest set from selected gameID
		String sql = "SELECT max(SETID) from PUBLIC.GAMESET WHERE GameID = iGameID";
		
		try {
			ResultSet res = stmt.executeQuery(sql);
			
			if(res.next()) {
				latestSetID = res.getInt(1);
			}
			res.close();
						
		} catch (SQLException e) {
			System.out.println("LOG: SQL Error");
		}
		
		//get starting player of last set in game
		sql = "SELECT StartingPlayer from PUBLIC.GAMESET where SetID = " + latestSetID;
		try {
			ResultSet res = stmt.executeQuery(sql);
			if(res.next()) {
				lastStartingPlayer = res.getString(1);
			}
			res.close();		
		} catch (SQLException e) {
			System.out.println("LOG: SQL Error");
		}
		
		if (lastStartingPlayer == "X") {
			newStartingPlayer = "O";
		}else if (lastStartingPlayer == "O") {
			newStartingPlayer = "X";
		}
		
		sql = "INSERT INTO \"PUBLIC\".\"GAMESET\" ( \"GAMEID\", \"STARTINGPLAYER\" ) VALUES ( " + iGameID + ", '" + newStartingPlayer + "')";
		
		sql = "SELECT max(SETID) from PUBLIC.GAMESET";
		try {
			
			ResultSet res = stmt.executeQuery(sql);
			
			if(res.next()) {
				setID = res.getInt(1);
			}
			res.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateScore(iGameID, GamePointsOwn, GamePointsOpponent);
		
		return setID; //SetID
	}
	
	/**
	 * Saves a turn of a specific game, set and player into the database.
	 * @param GameID
	 * @param SetID
	 * @param Player
	 * @param Column
	 */
	public void pushTurn(int GameID, int SetID, String Player, int Column) {
		
		//int turnID = 0;
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get latest set from selected gameID
		String sql = "INSERT INTO \"PUBLIC\".\"TURN\" ( \"GAMEID\", \"SETID\", \"TURN\", \"COLUMN\" ) VALUES ( " + GameID + ", " + SetID + ", '" + Player + "', " + Column + ")";
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: inserted new turn in db table turn");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		sql = "SELECT max(TurnID) from PUBLIC.TURN";
		try {
			ResultSet res = stmt.executeQuery(sql);
			if(res.next()) {
				turnID = res.getInt(1);
			}
			res.close();
			System.out.println("LOG: got turn id of newly created turn");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return turnID;
		*/
	}
	
	/**
	 * Gets all turns of a specific gameset and returns it together with the starting player in that set.
	 * @param GameID
	 * @param SetNumber
	 * @return integer array: <br>
	 * array[0] = 0 if we started, 1 of opponent started <br>
	 * array[1..n] = columns of turns
	 */
	public int[] getReplayTurns(int GameID, int SetNumber){
		
		// TODO
		// Include sets!!!!, Use 1,2,3 and not 12, 15, 27
		// TODO
		
		int totalRows = 0;
		int[] turns = null;
		int counter = 2;
		int startingPlayer = 0; // 0 = we started, 1 = opponent started
		int mapping[] = new int[getNumberOfSetsInGame(GameID)];
		int mappingCounter = 0;
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Mapping von eingegebener Satznummer zu SatzID in DB
		
		String sql = "SELECT * FROM PUBLIC.GAMESET WHERE GAMEID = " + GameID;
		
		try {
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				mapping[mappingCounter] = res.getInt(1);
				mappingCounter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Couldn't get sets of game");
		}
		
		PreparedStatement stmt2 = null;
		try {
			stmt2 = con.prepareStatement("SELECT * FROM PUBLIC.TURN WHERE GAMEID = " + GameID + " AND SETID = " + mapping[SetNumber - 1],
					   ResultSet.TYPE_SCROLL_SENSITIVE,
					   ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			
			ResultSet res = stmt2.executeQuery();
			
			if(res.next()) {
				res.last();
		        totalRows = res.getRow();
		        turns = new int[totalRows + 1];
		        res.beforeFirst();
		        
		        //get starting player and first turn
		        
		        if(res.next()) {
		        	
		        	if (res.getString(4) == "X") {
						startingPlayer = 0;
					}else if (res.getString(4) == "O") {
						startingPlayer = 1;
					}
		        	
		        	turns[0] = startingPlayer;
		        	turns[1] = res.getInt(5);
				}
		        
		        while (res.next()) {
					turns[counter] = res.getInt(5);
					counter++;
				}
			}
			res.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return turns;
	}
	
	/**
	 * Returns number of sets in a specific game
	 * @param GameID
	 * @return number of sets in game
	 */
	public int getNumberOfSetsInGame(int GameID){
		int number = 0;
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * Mapping von eingegebener Satznummer zu SatzID in DB
		 */
		PreparedStatement stmt2 = null;
		try {
			stmt2 = con.prepareStatement("SELECT * FROM PUBLIC.GAMESET WHERE GAMEID = " + GameID,
					   ResultSet.TYPE_SCROLL_SENSITIVE,
					   ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			ResultSet res = stmt2.executeQuery();
			
			if(res.next()) {
				res.last();
		        number = res.getRow();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Couldn't get sets of game");
		}
		
		return number;
	}
	
	/**
	 * Call to get all games of the database
	 * @return Raw ResultSet of all entries in the game table
	 * Column 1 = Opponent name
	 * Column 2 = Playtime
	 * Column 3 = GameID
	 */
	public ResultSet getGames(){
		ResultSet res = null;
		
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sql = "Select NAME, PLAYTIME, GAMEID from PUBLIC.GAME, PUBLIC.PLAYER where GAME.OPPONENTID = PLAYER.PLAYERID";
		
		try {
			res = stmt.executeQuery(sql);
			System.out.println("LOG: Got all games");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * Updates gamescore of an exisiting game, e.g. after a set
	 * @param GameID
	 * @param PointsOwn
	 * @param PointsOpponent
	 */
	private void updateScore(int GameID, int PointsOwn, int PointsOpponent){
		String sql = "UPDATE \"PUBLIC\".\"GAME\" SET (\"PointsOwn\", \"PointsOpponent\") = (" + PointsOwn + ", " + PointsOpponent + ") WHERE \"GameID\" = " + GameID;
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: Updates game entry with new score");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	public String getOpponentForGameID(int GameID){
		// Get OpponentID from GameID
		// Search for OpponentID in player.PlayerID
		// Return player.name of entry
		return "";
	}
	*/
}
